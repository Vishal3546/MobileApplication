package com.buysell.modules.settlement.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.settlement.entity.ReconciliationRecord;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.enums.ReconciliationStatus;
import com.buysell.modules.settlement.repository.ReconciliationRecordRepository;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.security.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class SettlementReconciliationService {

    @org.springframework.beans.factory.annotation.Autowired

    private ReconciliationRecordRepository reconciliationRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private ShopSettlementRepository settlementRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private CurrentUserService currentUserService;
    @org.springframework.beans.factory.annotation.Autowired
    private AuditService auditService;

    @Transactional
    public ReconciliationRecord reconcileSettlement(UUID settlementId, UUID shopId, BigDecimal actualAmount, String notes) {
        ShopSettlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new BusinessException("SETTLEMENT_NOT_FOUND", "Settlement not found", HttpStatus.NOT_FOUND));

        if (!currentUserService.hasPermission("RECONCILE_SETTLEMENT") && !currentUserService.isSuperAdmin()) {
            throw new BusinessException("ACCESS_DENIED", "You do not have permission to reconcile", HttpStatus.FORBIDDEN);
        }

        Shop shop;
        BigDecimal expectedAmount;

        if (settlement.getSourceShop().getId().equals(shopId)) {
            shop = settlement.getSourceShop();
            expectedAmount = settlement.getRemainingAmount(); // Receivable
        } else if (settlement.getDestinationShop().getId().equals(shopId)) {
            shop = settlement.getDestinationShop();
            expectedAmount = settlement.getRemainingAmount(); // Payable
        } else {
            throw new BusinessException("INVALID_SHOP", "Shop is not part of this settlement", HttpStatus.BAD_REQUEST);
        }

        BigDecimal difference = actualAmount.subtract(expectedAmount);
        ReconciliationStatus status = difference.compareTo(BigDecimal.ZERO) == 0 ? ReconciliationStatus.MATCHED : ReconciliationStatus.MISMATCHED;

        ReconciliationRecord record = ReconciliationRecord.builder()
                .settlement(settlement)
                .shop(shop)
                .expectedAmount(expectedAmount)
                .actualAmount(actualAmount)
                .difference(difference)
                .status(status)
                .notes(notes)
                .reconciledBy(currentUserService.getCurrentUser())
                .build();

        record = reconciliationRepository.save(record);

        auditService.logAction(currentUserService.getCurrentUserId(), shopId,
                "SETTLEMENT_RECONCILED", "ReconciliationRecord", record.getId(),
                null, null, null, "Settlement " + settlement.getSettlementNumber() + " reconciled with status " + status);

        return record;
    }
}
