package com.buysell.modules.purchase.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.purchase.dto.CreatePurchasePaymentRequest;
import com.buysell.modules.purchase.entity.PurchasePayment;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.PaymentStatus;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.repository.PurchasePaymentRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchasePaymentService {

    private final PurchasePaymentRepository paymentRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public PurchasePayment processPayment(PurchaseTransaction purchase, CreatePurchasePaymentRequest request) {
        if (purchase.getTransactionStatus() == TransactionStatus.COMPLETED || 
            purchase.getTransactionStatus() == TransactionStatus.CANCELLED) {
            throw new BusinessException("PURCHASE_INVALID_STATE", "Cannot add payment to a completed or cancelled purchase.", HttpStatus.BAD_REQUEST);
        }

        // Idempotency check
        Optional<PurchasePayment> existingPayment = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        PurchasePayment payment = PurchasePayment.builder()
                .purchaseTransaction(purchase)
                .paymentMode(request.getPaymentMode())
                .amount(request.getAmount())
                .referenceNumber(request.getReferenceNumber())
                .idempotencyKey(request.getIdempotencyKey())
                .paymentStatus(PaymentStatus.SUCCESS) // Assuming synchronous success for Phase 5
                .transactionTime(LocalDateTime.now())
                .processedBy(currentUserService.getCurrentUser())
                .build();

        payment = paymentRepository.save(payment);

        auditService.logAction(
                currentUserService.getCurrentUserId(),
                purchase.getBranch().getId(),
                "PURCHASE_PAYMENT_SUCCEEDED",
                "PurchasePayment",
                payment.getId(),
                purchase.getCustomer().getId().toString(),
                null,
                null,
                "Processed payment of " + payment.getAmount() + " via " + payment.getPaymentMode()
        );

        return payment;
    }

    @Transactional(readOnly = true)
    public List<PurchasePayment> getPaymentsForPurchase(UUID purchaseId) {
        return paymentRepository.findByPurchaseTransactionIdOrderByCreatedAtDesc(purchaseId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSuccessfulPayments(UUID purchaseId) {
        return paymentRepository.findByPurchaseTransactionIdOrderByCreatedAtDesc(purchaseId).stream()
                .filter(p -> p.getPaymentStatus() == PaymentStatus.SUCCESS)
                .map(PurchasePayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
