package com.buysell.modules.settlement.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.settlement.entity.SettlementDispute;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.enums.DisputeReason;
import com.buysell.modules.settlement.enums.DisputeStatus;
import com.buysell.modules.settlement.enums.SettlementStatus;
import com.buysell.modules.settlement.repository.SettlementDisputeRepository;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.security.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
public class SettlementDisputeService {

    @org.springframework.beans.factory.annotation.Autowired

    private SettlementDisputeRepository disputeRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private ShopSettlementRepository settlementRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private CurrentUserService currentUserService;
    @org.springframework.beans.factory.annotation.Autowired
    private AuditService auditService;

    @Transactional
    public SettlementDispute raiseDispute(UUID settlementId, DisputeReason reason, BigDecimal claimedAmount) {
        ShopSettlement settlement = settlementRepository.findByIdWithLock(settlementId)
                .orElseThrow(() -> new BusinessException("SETTLEMENT_NOT_FOUND", "Settlement not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.hasPermission("RAISE_SETTLEMENT_DISPUTE") && !currentUserService.isSuperAdmin()) {
            throw new BusinessException("ACCESS_DENIED", "You do not have permission to raise a dispute", HttpStatus.FORBIDDEN);
        }

        if (settlement.getStatus() == SettlementStatus.SETTLED || settlement.getStatus() == SettlementStatus.CANCELLED) {
            throw new BusinessException("SETTLEMENT_INVALID_STATE", "Cannot dispute a closed settlement", HttpStatus.BAD_REQUEST);
        }

        SettlementDispute dispute = SettlementDispute.builder()
                .settlement(settlement)
                .raisedBy(currentUserService.getCurrentUser())
                .reason(reason)
                .claimedAmount(claimedAmount)
                .status(DisputeStatus.OPEN)
                .build();

        dispute = disputeRepository.save(dispute);

        settlement.setStatus(SettlementStatus.DISPUTED);
        settlementRepository.save(settlement);

        auditService.logAction(currentUserService.getCurrentUserId(), settlement.getDestinationShop().getId(),
                "SETTLEMENT_DISPUTE_RAISED", "SettlementDispute", dispute.getId(),
                null, null, null, "Dispute raised for settlement " + settlement.getSettlementNumber());

        return dispute;
    }

    @Transactional
    public SettlementDispute resolveDispute(UUID disputeId, String resolution) {
        SettlementDispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new BusinessException("DISPUTE_NOT_FOUND", "Dispute not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.hasPermission("RESOLVE_SETTLEMENT_DISPUTE") && !currentUserService.isSuperAdmin()) {
            throw new BusinessException("ACCESS_DENIED", "You do not have permission to resolve a dispute", HttpStatus.FORBIDDEN);
        }

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.REJECTED) {
            throw new BusinessException("DISPUTE_INVALID_STATE", "Dispute already closed", HttpStatus.BAD_REQUEST);
        }

        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setResolution(resolution);
        dispute.setResolvedBy(currentUserService.getCurrentUser());
        dispute.setResolvedAt(ZonedDateTime.now());
        
        dispute = disputeRepository.save(dispute);

        ShopSettlement settlement = dispute.getSettlement();
        // Restore status based on remaining amount
        if (settlement.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            settlement.setStatus(SettlementStatus.SETTLED);
        } else if (settlement.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            settlement.setStatus(SettlementStatus.PARTIALLY_PAID);
        } else {
            settlement.setStatus(SettlementStatus.PENDING);
        }
        settlementRepository.save(settlement);

        auditService.logAction(currentUserService.getCurrentUserId(), settlement.getDestinationShop().getId(),
                "SETTLEMENT_DISPUTE_RESOLVED", "SettlementDispute", dispute.getId(),
                null, null, null, "Dispute resolved for settlement " + settlement.getSettlementNumber());

        return dispute;
    }
}
