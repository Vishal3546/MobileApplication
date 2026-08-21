package com.buysell.modules.sales.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.modules.inventory.dto.ReserveInventoryRequest;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.inventory.service.InventoryService;
import com.buysell.modules.sales.dto.CreateSaleRequest;
import com.buysell.modules.sales.dto.OverrideSalePriceRequest;
import com.buysell.modules.sales.dto.SaleTransactionResponse;
import com.buysell.modules.sales.entity.SaleStatusHistory;
import com.buysell.modules.sales.entity.SaleTransaction;
import com.buysell.modules.sales.entity.SalesInvoice;
import com.buysell.modules.sales.enums.PaymentStatus;
import com.buysell.modules.sales.enums.SaleStatus;
import com.buysell.modules.sales.repository.SaleStatusHistoryRepository;
import com.buysell.modules.sales.repository.SaleTransactionRepository;
import com.buysell.modules.sales.repository.SalesInvoiceRepository;
import com.buysell.security.CurrentUserService;

@Service
public class SaleService {

    private final SaleTransactionRepository saleTransactionRepository;
    private final SaleStatusHistoryRepository saleStatusHistoryRepository;
    private final SalesInvoiceRepository salesInvoiceRepository;
    private final CustomerRepository customerRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryService inventoryService;
    private final SalePricingService salePricingService;
    private final WarrantyPolicyService warrantyPolicyService;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Value("${sale.reservation.default-minutes:30}")
    private int defaultReservationMinutes;

    public SaleService(SaleTransactionRepository saleTransactionRepository,
                       SaleStatusHistoryRepository saleStatusHistoryRepository,
                       SalesInvoiceRepository salesInvoiceRepository,
                       CustomerRepository customerRepository,
                       InventoryItemRepository inventoryItemRepository,
                       InventoryService inventoryService,
                       SalePricingService salePricingService,
                       WarrantyPolicyService warrantyPolicyService,
                       CurrentUserService currentUserService,
                       AuditService auditService) {
        this.saleTransactionRepository = saleTransactionRepository;
        this.saleStatusHistoryRepository = saleStatusHistoryRepository;
        this.salesInvoiceRepository = salesInvoiceRepository;
        this.customerRepository = customerRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryService = inventoryService;
        this.salePricingService = salePricingService;
        this.warrantyPolicyService = warrantyPolicyService;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @Transactional
    public SaleTransactionResponse createSale(CreateSaleRequest request) {
        if (!currentUserService.hasPermission("CREATE_SALE")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to create sale", HttpStatus.FORBIDDEN);
        }

        // Validate Customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("CUSTOMER_NOT_FOUND", "Customer not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.isSuperAdmin() && !customer.getBranch().getId().equals(currentUserService.getCurrentBranch().getId())) {
            throw new BusinessException("CUSTOMER_ACCESS_DENIED", "Access to this customer is denied", HttpStatus.FORBIDDEN);
        }

        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("CUSTOMER_BLOCKED", "Customer is blocked", HttpStatus.BAD_REQUEST);
        }

        // We lock inventory and reserve it. inventoryService.reserveInventory gets the lock.
        // Wait, if we use inventoryService.reserveInventory, it's a separate service call. Since it's transactional, it shares the lock.
        InventoryItem inventory = inventoryItemRepository.findByIdWithLock(request.getInventoryItemId())
                .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Inventory not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.isSuperAdmin() && !inventory.getBranch().getId().equals(currentUserService.getCurrentBranch().getId())) {
            throw new BusinessException("SALE_BRANCH_ACCESS_DENIED", "Access to this inventory is denied", HttpStatus.FORBIDDEN);
        }

        ReserveInventoryRequest reserveReq = new ReserveInventoryRequest();
        reserveReq.setReservedUntil(ZonedDateTime.now().plus(defaultReservationMinutes, ChronoUnit.MINUTES));
        
        // This validates AVAILABLE status and branch access inside
        inventoryService.reserveInventory(inventory.getId(), reserveReq);

        salePricingService.validateDiscount(request.getSellingPrice(), request.getDiscountAmount());

        SaleTransaction sale = SaleTransaction.builder()
                .saleNumber(saleTransactionRepository.generateSaleNumber())
                .customer(customer)
                .inventoryItem(inventory)
                .employee(currentUserService.getCurrentUser())
                .branch(inventory.getBranch())
                .sellingPrice(request.getSellingPrice())
                .discountAmount(request.getDiscountAmount())
                .taxAmount(request.getTaxAmount())
                .finalAmount(salePricingService.calculateFinalAmount(request.getSellingPrice(), request.getDiscountAmount(), request.getTaxAmount()))
                .saleStatus(SaleStatus.RESERVED)
                .paymentStatus(PaymentStatus.PENDING)
                .notes(request.getNotes())
                .createdBy(currentUserService.getCurrentUser())
                .build();

        sale = saleTransactionRepository.save(sale);

        recordHistory(sale, null, SaleStatus.RESERVED, "Sale created and inventory reserved");

        auditService.logAction(currentUserService.getCurrentUserId(), sale.getBranch().getId(),
                "SALE_CREATED", "SaleTransaction", sale.getId(),
                sale.getSaleNumber(), null,
                null, "Sale created and reserved");

        return mapToResponse(sale);
    }

    @Transactional
    public SaleTransactionResponse overridePrice(UUID saleId, OverrideSalePriceRequest request) {
        salePricingService.validatePriceOverride();
        
        SaleTransaction sale = saleTransactionRepository.findByIdWithLock(saleId)
                .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale transaction not found", HttpStatus.NOT_FOUND));
        
        if (sale.getSaleStatus() == SaleStatus.COMPLETED || sale.getSaleStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("SALE_INVALID_STATE", "Cannot override price of a completed or cancelled sale", HttpStatus.BAD_REQUEST);
        }

        sale.setSellingPrice(request.getSellingPrice());
        sale.setFinalAmount(salePricingService.calculateFinalAmount(sale.getSellingPrice(), sale.getDiscountAmount(), sale.getTaxAmount()));
        
        sale = saleTransactionRepository.save(sale);

        auditService.logAction(currentUserService.getCurrentUserId(), sale.getBranch().getId(),
                "SALE_PRICE_OVERRIDDEN", "SaleTransaction", sale.getId(),
                sale.getSaleNumber(), null,
                null, "Sale price overridden to " + request.getSellingPrice());

        return mapToResponse(sale);
    }

    @Transactional
    public SaleTransactionResponse completeSale(UUID saleId) {
        if (!currentUserService.hasPermission("COMPLETE_SALE")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to complete sale", HttpStatus.FORBIDDEN);
        }

        SaleTransaction sale = saleTransactionRepository.findByIdWithLock(saleId)
                .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale transaction not found", HttpStatus.NOT_FOUND));

        if (sale.getSaleStatus() == SaleStatus.COMPLETED) {
            throw new BusinessException("SALE_ALREADY_COMPLETED", "Sale is already completed", HttpStatus.BAD_REQUEST);
        }
        if (sale.getSaleStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("SALE_ALREADY_CANCELLED", "Sale is cancelled", HttpStatus.BAD_REQUEST);
        }

        if (sale.getPaymentStatus() != PaymentStatus.SUCCESS && sale.getSaleStatus() != SaleStatus.PAID) {
            throw new BusinessException("SALE_PAYMENT_REQUIRED", "Full payment is required to complete the sale", HttpStatus.BAD_REQUEST);
        }

        // Re-verify customer and inventory
        Customer customer = sale.getCustomer();
        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("CUSTOMER_BLOCKED", "Customer is blocked", HttpStatus.BAD_REQUEST);
        }

        InventoryItem inventory = sale.getInventoryItem();
        if (inventory.getStatus() != InventoryStatus.RESERVED) {
            throw new BusinessException("SALE_INVALID_STATE", "Inventory is not reserved for this sale", HttpStatus.BAD_REQUEST);
        }
        if (inventory.getReservedUntil() != null && inventory.getReservedUntil().isBefore(ZonedDateTime.now())) {
            throw new BusinessException("SALE_RESERVATION_EXPIRED", "Inventory reservation has expired", HttpStatus.BAD_REQUEST);
        }

        // Change Inventory status
        // Since we didn't implement explicit release logic that skips the AVAILABLE state constraint,
        // we'll update it directly using the repository or a service method.
        inventory.setStatus(InventoryStatus.SOLD);
        inventory.setReservedUntil(null);
        inventory.setReservedBy(null);
        inventoryItemRepository.save(inventory);

        // Update Sale
        SaleStatus previousStatus = sale.getSaleStatus();
        sale.setSaleStatus(SaleStatus.COMPLETED);
        sale.setWarrantyStartDate(ZonedDateTime.now());
        sale.setWarrantyEndDate(warrantyPolicyService.calculateWarrantyEndDate(sale.getWarrantyStartDate()));
        sale = saleTransactionRepository.save(sale);

        // Create Invoice
        SalesInvoice invoice = SalesInvoice.builder()
                .saleTransaction(sale)
                .invoiceNumber(salesInvoiceRepository.generateInvoiceNumber())
                .issuedBy(currentUserService.getCurrentUser())
                .build();
        salesInvoiceRepository.save(invoice);

        recordHistory(sale, previousStatus, SaleStatus.COMPLETED, "Sale completed successfully");

        auditService.logAction(currentUserService.getCurrentUserId(), sale.getBranch().getId(),
                "SALE_COMPLETED", "SaleTransaction", sale.getId(),
                sale.getSaleNumber(), null,
                null, "Sale completed and invoice generated: " + invoice.getInvoiceNumber());

        return mapToResponse(sale);
    }

    @Transactional
    public SaleTransactionResponse cancelSale(UUID saleId, String reason) {
        if (!currentUserService.hasPermission("CANCEL_SALE")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to cancel sale", HttpStatus.FORBIDDEN);
        }

        SaleTransaction sale = saleTransactionRepository.findByIdWithLock(saleId)
                .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale transaction not found", HttpStatus.NOT_FOUND));

        if (sale.getSaleStatus() == SaleStatus.COMPLETED) {
            throw new BusinessException("SALE_ALREADY_COMPLETED", "Cannot cancel a completed sale without a return process", HttpStatus.BAD_REQUEST);
        }
        if (sale.getSaleStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("SALE_ALREADY_CANCELLED", "Sale is already cancelled", HttpStatus.BAD_REQUEST);
        }

        SaleStatus previousStatus = sale.getSaleStatus();
        sale.setSaleStatus(SaleStatus.CANCELLED);
        sale = saleTransactionRepository.save(sale);

        // Release inventory if it was reserved
        InventoryItem inventory = sale.getInventoryItem();
        if (inventory.getStatus() == InventoryStatus.RESERVED && 
            inventory.getReservedBy() != null && 
            inventory.getReservedBy().getId().equals(sale.getEmployee().getId())) {
            inventoryService.releaseInventory(inventory.getId());
        }

        recordHistory(sale, previousStatus, SaleStatus.CANCELLED, reason);

        auditService.logAction(currentUserService.getCurrentUserId(), sale.getBranch().getId(),
                "SALE_CANCELLED", "SaleTransaction", sale.getId(),
                sale.getSaleNumber(), null,
                null, "Sale cancelled. Reason: " + reason);

        return mapToResponse(sale);
    }

    @Transactional
    public void expireSaleReservation(UUID saleId) {
        SaleTransaction sale = saleTransactionRepository.findByIdWithLock(saleId)
                .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale transaction not found", HttpStatus.NOT_FOUND));

        if (sale.getSaleStatus() == SaleStatus.COMPLETED || sale.getSaleStatus() == SaleStatus.CANCELLED) {
            return;
        }

        InventoryItem inventory = sale.getInventoryItem();
        if (inventory.getReservedUntil() != null && inventory.getReservedUntil().isBefore(ZonedDateTime.now())) {
            SaleStatus previousStatus = sale.getSaleStatus();
            sale.setSaleStatus(SaleStatus.CANCELLED);
            saleTransactionRepository.save(sale);

            if (inventory.getStatus() == InventoryStatus.RESERVED) {
                inventoryService.releaseInventory(inventory.getId());
            }

            recordHistory(sale, previousStatus, SaleStatus.CANCELLED, "Reservation expired");

            auditService.logAction(sale.getEmployee().getId(), sale.getBranch().getId(),
                    "SALE_CANCELLED", "SaleTransaction", sale.getId(),
                    sale.getSaleNumber(), null,
                    null, "Sale cancelled due to reservation expiry");
        }
    }

    private void recordHistory(SaleTransaction sale, SaleStatus prev, SaleStatus next, String reason) {
        SaleStatusHistory history = SaleStatusHistory.builder()
                .saleTransaction(sale)
                .previousStatus(prev)
                .newStatus(next)
                .reason(reason)
                .changedBy(currentUserService.getCurrentUser())
                .branch(sale.getBranch())
                .build();
        saleStatusHistoryRepository.save(history);
    }

    private SaleTransactionResponse mapToResponse(SaleTransaction sale) {
        SaleTransactionResponse res = new SaleTransactionResponse();
        res.setId(sale.getId());
        res.setSaleNumber(sale.getSaleNumber());
        res.setCustomerId(sale.getCustomer().getId());
        res.setInventoryItemId(sale.getInventoryItem().getId());
        res.setEmployeeId(sale.getEmployee().getId());
        res.setBranchId(sale.getBranch().getId());
        res.setSellingPrice(sale.getSellingPrice());
        res.setDiscountAmount(sale.getDiscountAmount());
        res.setTaxAmount(sale.getTaxAmount());
        res.setFinalAmount(sale.getFinalAmount());
        res.setCurrency(sale.getCurrency());
        res.setSaleStatus(sale.getSaleStatus());
        res.setPaymentStatus(sale.getPaymentStatus());
        res.setWarrantyStartDate(sale.getWarrantyStartDate());
        res.setWarrantyEndDate(sale.getWarrantyEndDate());
        res.setReturnPolicyCode(sale.getReturnPolicyCode());
        res.setNotes(sale.getNotes());
        res.setCreatedAt(sale.getCreatedAt());
        res.setUpdatedAt(sale.getUpdatedAt());
        return res;
    }
}
