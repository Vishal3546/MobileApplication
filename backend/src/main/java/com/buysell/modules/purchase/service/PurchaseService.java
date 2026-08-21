package com.buysell.modules.purchase.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.entity.CustomerConsent;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.enums.VerificationStatus;
import com.buysell.modules.customer.repository.CustomerConsentRepository;
import com.buysell.modules.customer.service.CustomerService;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.enums.DeviceStatus;
import com.buysell.modules.device.service.DeviceConditionService;
import com.buysell.modules.device.service.DeviceInspectionService;
import com.buysell.modules.device.service.DeviceService;
import com.buysell.modules.purchase.dto.CreatePurchaseRequest;
import com.buysell.modules.purchase.entity.PurchaseStatusHistory;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.repository.PurchaseStatusHistoryRepository;
import com.buysell.modules.purchase.repository.PurchaseTransactionRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseTransactionRepository purchaseRepository;
    private final PurchaseStatusHistoryRepository statusHistoryRepository;
    private final CustomerService customerService;
    private final DeviceService deviceService;
    private final DeviceConditionService conditionService;
    private final DeviceInspectionService inspectionService;
    private final CustomerConsentRepository consentRepository;
    private final PurchaseStatusService statusService;
    private final PurchasePricingService pricingService;
    private final PurchasePaymentService paymentService;
    private final PurchaseReceiptService receiptService;
    private final com.buysell.modules.inventory.service.InventoryCreationService inventoryCreationService;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public PurchaseTransaction createPurchase(CreatePurchaseRequest request) {
        // 1. Validate Customer
        Customer customer = customerService.getAndValidateAccess(request.getCustomerId());
        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("CUSTOMER_BLOCKED", "Cannot purchase from a blocked customer.", HttpStatus.BAD_REQUEST);
        }

        // 2. Validate Device (With Lock for Concurrency Protection)
        Device device = deviceService.getDeviceByIdWithLock(request.getDeviceId());
        if (device.getStatus() == DeviceStatus.BLOCKED) {
            throw new BusinessException("DEVICE_BLOCKED", "Cannot purchase a blocked device.", HttpStatus.BAD_REQUEST);
        }

        // 3. Active purchase duplicate check
        if (purchaseRepository.hasActivePurchaseForDevice(device.getId(), statusService.getTerminalStatuses())) {
            throw new BusinessException("PURCHASE_ACTIVE", "This device already has an active purchase transaction.", HttpStatus.BAD_REQUEST);
        }

        // 4. Validate Prices
        pricingService.validatePrices(request.getSuggestedPrice(), request.getNegotiatedPrice(), request.getFinalPrice());

        // Generate Number (Concurrency Safe Sequence)
        String purchaseNumber = "PUR-" + java.time.Year.now().getValue() + "-" + 
                String.format("%06d", purchaseRepository.getNextPurchaseNumberSequence());

        Branch branch = currentUserService.getCurrentBranch();

        PurchaseTransaction purchase = PurchaseTransaction.builder()
                .purchaseNumber(purchaseNumber)
                .customer(customer)
                .device(device)
                .employee(currentUserService.getCurrentUser())
                .branch(branch)
                .suggestedPrice(request.getSuggestedPrice())
                .negotiatedPrice(request.getNegotiatedPrice())
                .finalPrice(request.getFinalPrice())
                .notes(request.getNotes())
                .transactionStatus(TransactionStatus.INITIATED)
                .createdBy(currentUserService.getCurrentUser())
                .build();

        purchase = purchaseRepository.save(purchase);

        recordStatusHistory(purchase, null, TransactionStatus.INITIATED, "Purchase initiated");
        
        auditService.logAction(
                currentUserService.getCurrentUserId(),
                branch.getId(),
                "PURCHASE_CREATED",
                "PurchaseTransaction",
                purchase.getId(),
                customer.getId().toString(),
                device.getId().toString(),
                null,
                "Purchase " + purchaseNumber + " created."
        );

        return purchase;
    }

    @Transactional
    public PurchaseTransaction transitionStatus(UUID purchaseId, TransactionStatus newStatus, String reason) {
        PurchaseTransaction purchase = getAndValidateAccessWithLock(purchaseId);
        TransactionStatus currentStatus = purchase.getTransactionStatus();

        statusService.validateTransition(currentStatus, newStatus);

        purchase.setTransactionStatus(newStatus);
        purchase = purchaseRepository.save(purchase);

        recordStatusHistory(purchase, currentStatus, newStatus, reason);
        
        auditService.logAction(
                currentUserService.getCurrentUserId(),
                purchase.getBranch().getId(),
                "PURCHASE_STATUS_CHANGED",
                "PurchaseTransaction",
                purchase.getId(),
                purchase.getCustomer().getId().toString(),
                purchase.getDevice().getId().toString(),
                null,
                "Status changed to " + newStatus
        );

        return purchase;
    }

    @Transactional
    public void completePurchase(UUID purchaseId) {
        // Lock the transaction to prevent concurrent completion attempts
        PurchaseTransaction purchase = getAndValidateAccessWithLock(purchaseId);
        statusService.validateTransition(purchase.getTransactionStatus(), TransactionStatus.COMPLETED);

        // Checklist validation
        validateCompletionPrerequisites(purchase);

        // Transition
        purchase.setTransactionStatus(TransactionStatus.COMPLETED);
        purchase = purchaseRepository.save(purchase);
        recordStatusHistory(purchase, TransactionStatus.PENDING_PAYMENT, TransactionStatus.COMPLETED, "Purchase completed successfully");

        // Receipt Generation
        receiptService.generateReceipt(purchase);

        // Inventory Creation
        inventoryCreationService.createInventoryFromPurchase(purchase);

        auditService.logAction(
                currentUserService.getCurrentUserId(),
                purchase.getBranch().getId(),
                "PURCHASE_COMPLETED",
                "PurchaseTransaction",
                purchase.getId(),
                purchase.getCustomer().getId().toString(),
                purchase.getDevice().getId().toString(),
                null,
                "Purchase completed."
        );
    }

    private void validateCompletionPrerequisites(PurchaseTransaction purchase) {
        // Customer
        if (purchase.getCustomer().getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("CUSTOMER_BLOCKED", "Customer is blocked.", HttpStatus.BAD_REQUEST);
        }
        if (purchase.getCustomer().getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new BusinessException("CUSTOMER_NOT_VERIFIED", "Customer KYC is not VERIFIED.", HttpStatus.BAD_REQUEST);
        }

        // Device
        if (purchase.getDevice().getStatus() == DeviceStatus.BLOCKED) {
            throw new BusinessException("DEVICE_BLOCKED", "Device is blocked.", HttpStatus.BAD_REQUEST);
        }

        // Condition & Inspection check
        if (conditionService.getConditionHistory(purchase.getDevice().getId()).isEmpty()) {
            throw new BusinessException("CONDITION_REQUIRED", "Device condition record is required.", HttpStatus.BAD_REQUEST);
        }
        if (inspectionService.getInspectionHistory(purchase.getDevice().getId()).isEmpty()) {
            throw new BusinessException("INSPECTION_REQUIRED", "Device inspection record is required.", HttpStatus.BAD_REQUEST);
        }

        // Consent Check
        List<CustomerConsent> consents = consentRepository.findByReferenceTypeAndReferenceId("PURCHASE", purchase.getId());
        if (consents.isEmpty()) {
            throw new BusinessException("CONSENT_REQUIRED", "Purchase consent is required.", HttpStatus.BAD_REQUEST);
        }

        // Final Price
        if (purchase.getFinalPrice() == null || purchase.getFinalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE", "Final price must be set and non-negative.", HttpStatus.BAD_REQUEST);
        }

        // Payment Total
        BigDecimal totalPayments = paymentService.calculateTotalSuccessfulPayments(purchase.getId());
        if (totalPayments.compareTo(purchase.getFinalPrice()) != 0) {
            throw new BusinessException("PAYMENT_AMOUNT_MISMATCH", 
                "Total successful payments (" + totalPayments + ") must equal final price (" + purchase.getFinalPrice() + ").", 
                HttpStatus.BAD_REQUEST);
        }
    }

    private void recordStatusHistory(PurchaseTransaction purchase, TransactionStatus previous, TransactionStatus current, String reason) {
        PurchaseStatusHistory history = PurchaseStatusHistory.builder()
                .purchaseTransaction(purchase)
                .previousStatus(previous)
                .newStatus(current)
                .reason(reason)
                .changedBy(currentUserService.getCurrentUser())
                .branch(purchase.getBranch())
                .build();
        statusHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public PurchaseTransaction getAndValidateAccess(UUID purchaseId) {
        PurchaseTransaction purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new BusinessException("PURCHASE_NOT_FOUND", "Purchase not found", HttpStatus.NOT_FOUND));
        return validateAccess(purchase);
    }
    
    @Transactional
    public PurchaseTransaction getAndValidateAccessWithLock(UUID purchaseId) {
        PurchaseTransaction purchase = purchaseRepository.findByIdWithLock(purchaseId)
                .orElseThrow(() -> new BusinessException("PURCHASE_NOT_FOUND", "Purchase not found", HttpStatus.NOT_FOUND));
        return validateAccess(purchase);
    }
    
    private PurchaseTransaction validateAccess(PurchaseTransaction purchase) {
        if (!currentUserService.hasPermission("SUPER_ADMIN")) {
            UUID currentUserBranchId = currentUserService.getCurrentBranch().getId();
            if (!purchase.getBranch().getId().equals(currentUserBranchId)) {
                throw new BusinessException("CUSTOMER_ACCESS_DENIED", "Access denied to branch purchase.", HttpStatus.FORBIDDEN);
            }
        }
        return purchase;
    }
}
