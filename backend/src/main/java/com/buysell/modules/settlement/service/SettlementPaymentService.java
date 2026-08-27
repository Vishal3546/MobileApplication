package com.buysell.modules.settlement.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.settlement.entity.SettlementPayment;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.enums.SettlementPaymentMode;
import com.buysell.modules.settlement.enums.SettlementStatus;
import com.buysell.modules.settlement.repository.SettlementPaymentRepository;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.security.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SettlementPaymentService {

    @org.springframework.beans.factory.annotation.Autowired

    private ShopSettlementRepository settlementRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private SettlementPaymentRepository paymentRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private CurrentUserService currentUserService;
    @org.springframework.beans.factory.annotation.Autowired
    private AuditService auditService;

    @Transactional
    public SettlementPayment createPayment(UUID settlementId, BigDecimal amount, SettlementPaymentMode mode, String refNum, String idempotencyKey) {
        
        Optional<SettlementPayment> existingPayment = paymentRepository.findBySettlementIdAndIdempotencyKey(settlementId, idempotencyKey);
        if (existingPayment.isPresent()) {
            return existingPayment.get();
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("PAYMENT_AMOUNT_INVALID", "Payment amount must be greater than zero", HttpStatus.BAD_REQUEST);
        }

        ShopSettlement settlement = settlementRepository.findByIdWithLock(settlementId)
                .orElseThrow(() -> new BusinessException("SETTLEMENT_NOT_FOUND", "Settlement not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.hasAccessToBranch(settlement.getDestinationShop().getBranches().stream().findFirst().orElse(null)) 
            && !currentUserService.hasAccessToBranch(settlement.getSourceShop().getBranches().stream().findFirst().orElse(null))
            && !currentUserService.isSuperAdmin()) {
            throw new BusinessException("ACCESS_DENIED", "Access denied to settlement", HttpStatus.FORBIDDEN);
        }

        if (settlement.getStatus() == SettlementStatus.SETTLED || settlement.getStatus() == SettlementStatus.CANCELLED) {
            throw new BusinessException("SETTLEMENT_INVALID_STATE", "Cannot make payments to a " + settlement.getStatus() + " settlement", HttpStatus.BAD_REQUEST);
        }

        if (amount.compareTo(settlement.getRemainingAmount()) > 0) {
            throw new BusinessException("PAYMENT_AMOUNT_EXCEEDED", "Payment amount exceeds remaining balance", HttpStatus.BAD_REQUEST);
        }

        SettlementPayment payment = SettlementPayment.builder()
                .settlement(settlement)
                .amount(amount)
                .paymentMode(mode)
                .referenceNumber(refNum)
                .idempotencyKey(idempotencyKey)
                .createdBy(currentUserService.getCurrentUser())
                .build();

        payment = paymentRepository.save(payment);

        settlement.setPaidAmount(settlement.getPaidAmount().add(amount));
        settlement.setRemainingAmount(settlement.getGrossAmount().subtract(settlement.getPaidAmount()));
        
        if (settlement.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            settlement.setStatus(SettlementStatus.SETTLED);
        } else {
            settlement.setStatus(SettlementStatus.PARTIALLY_PAID);
        }

        settlementRepository.save(settlement);

        auditService.logAction(currentUserService.getCurrentUserId(), settlement.getDestinationShop().getId(),
                "SETTLEMENT_PAYMENT_CREATED", "SettlementPayment", payment.getId(),
                null, null, null, "Payment of " + amount + " made for settlement " + settlement.getSettlementNumber());

        return payment;
    }
}
