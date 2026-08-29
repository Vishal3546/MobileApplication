package com.buysell.modules.sales.service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.sales.dto.CreateSalePaymentRequest;
import com.buysell.modules.sales.dto.SalePaymentResponse;
import com.buysell.modules.sales.entity.SalePayment;
import com.buysell.modules.sales.entity.SaleTransaction;
import com.buysell.modules.sales.enums.PaymentStatus;
import com.buysell.modules.sales.enums.SaleStatus;
import com.buysell.modules.sales.repository.SalePaymentRepository;
import com.buysell.modules.sales.repository.SaleTransactionRepository;
import com.buysell.security.CurrentUserService;

@Service
public class SalePaymentService {

    private final SalePaymentRepository salePaymentRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public SalePaymentService(SalePaymentRepository salePaymentRepository, 
                              SaleTransactionRepository saleTransactionRepository,
                              CurrentUserService currentUserService,
                              AuditService auditService) {
        this.salePaymentRepository = salePaymentRepository;
        this.saleTransactionRepository = saleTransactionRepository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @Transactional
    public SalePaymentResponse createPayment(CreateSalePaymentRequest request) {
        if (!currentUserService.hasPermission("CREATE_SALE_PAYMENT")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to create sale payment", HttpStatus.FORBIDDEN);
        }

        // Idempotency check
        Optional<SalePayment> existingPayment = salePaymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingPayment.isPresent()) {
            return mapToResponse(existingPayment.get());
        }

        // Lock SaleTransaction for payment consistency
        SaleTransaction sale = saleTransactionRepository.findByIdWithLock(request.getSaleTransactionId())
                .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale transaction not found", HttpStatus.NOT_FOUND));

        // Validate Branch Access
        if (!currentUserService.isSuperAdmin() && !sale.getBranch().getId().equals(currentUserService.getCurrentBranch().getId())) {
            throw new BusinessException("SALE_BRANCH_ACCESS_DENIED", "Cannot process payment for a sale in another branch", HttpStatus.FORBIDDEN);
        }

        if (sale.getSaleStatus() == SaleStatus.COMPLETED || sale.getSaleStatus() == SaleStatus.CANCELLED) {
            throw new BusinessException("SALE_INVALID_STATE", "Cannot add payment to a completed or cancelled sale", HttpStatus.BAD_REQUEST);
        }

        // Calculate sum of successful payments
        List<SalePayment> successfulPayments = salePaymentRepository.findBySaleTransactionIdAndPaymentStatus(sale.getId(), PaymentStatus.SUCCESS);
        BigDecimal sumPaid = successfulPayments.stream().map(SalePayment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sumPaid.add(request.getAmount()).compareTo(sale.getFinalAmount()) > 0) {
            throw new BusinessException("SALE_PAYMENT_AMOUNT_MISMATCH", "Payment amount exceeds remaining balance", HttpStatus.BAD_REQUEST);
        }

        SalePayment payment = SalePayment.builder()
                .saleTransaction(sale)
                .paymentMode(request.getPaymentMode())
                .amount(request.getAmount())
                .paymentStatus(PaymentStatus.SUCCESS) // Assuming success for now. Real gateways would update asynchronously.
                .referenceNumber(request.getReferenceNumber())
                .idempotencyKey(request.getIdempotencyKey())
                .transactionTime(ZonedDateTime.now())
                .processedBy(currentUserService.getCurrentUser())
                .build();

        payment = salePaymentRepository.save(payment);

        auditService.logAction(currentUserService.getCurrentUserId(), sale.getBranch().getId(),
                "SALE_PAYMENT_SUCCEEDED", "SaleTransaction", sale.getId(),
                sale.getSaleNumber(), null,
                null, "Payment of " + payment.getAmount() + " processed via " + payment.getPaymentMode());

        if (sale.getSaleStatus() == SaleStatus.INITIATED || sale.getSaleStatus() == SaleStatus.RESERVED) {
            sale.setSaleStatus(SaleStatus.PENDING_PAYMENT);
            saleTransactionRepository.save(sale);
        }
        
        sumPaid = sumPaid.add(payment.getAmount());
        if (sumPaid.compareTo(sale.getFinalAmount()) == 0 && sale.getSaleStatus() != SaleStatus.PAID) {
            sale.setSaleStatus(SaleStatus.PAID);
            saleTransactionRepository.save(sale);
        }

        return mapToResponse(payment);
    }

    private SalePaymentResponse mapToResponse(SalePayment payment) {
        SalePaymentResponse response = new SalePaymentResponse();
        response.setId(payment.getId());
        response.setSaleTransactionId(payment.getSaleTransaction().getId());
        response.setPaymentMode(payment.getPaymentMode());
        response.setAmount(payment.getAmount());
        response.setPaymentStatus(payment.getPaymentStatus());
        response.setReferenceNumber(payment.getReferenceNumber());
        response.setIdempotencyKey(payment.getIdempotencyKey());
        response.setTransactionTime(payment.getTransactionTime());
        response.setProcessedById(payment.getProcessedBy().getId());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
    @Transactional(readOnly = true)
    public List<SalePaymentResponse> getPaymentsForSale(UUID saleId) {
        SaleTransaction sale = saleTransactionRepository.findById(saleId)
                .orElseThrow(() -> new BusinessException("SALE_NOT_FOUND", "Sale not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.hasAccessToBranch(sale.getBranch())) {
            throw new BusinessException("SALE_ACCESS_DENIED", "Access denied to this sale", HttpStatus.FORBIDDEN);
        }

        return salePaymentRepository.findBySaleTransactionId(saleId).stream()
                .map(this::mapToResponse)
                .toList();
    }
}
