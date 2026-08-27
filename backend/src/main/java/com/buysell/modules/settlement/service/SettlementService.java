package com.buysell.modules.settlement.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.inventory.entity.StockTransfer;
import com.buysell.modules.inventory.entity.StockTransferType;
import com.buysell.modules.inventory.repository.StockTransferRepository;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.enums.SettlementStatus;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.security.CurrentUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import com.buysell.modules.inventory.event.StockTransferCompletedEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class SettlementService {

    @org.springframework.beans.factory.annotation.Autowired

    private ShopSettlementRepository settlementRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private StockTransferRepository transferRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private CurrentUserService currentUserService;
    @org.springframework.beans.factory.annotation.Autowired
    private AuditService auditService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onStockTransferCompleted(StockTransferCompletedEvent event) {
        createSettlementFromTransfer(event.getTransferId());
    }

    @Transactional
    public void createSettlementFromTransfer(UUID transferId) {
        StockTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new BusinessException("TRANSFER_NOT_FOUND", "Transfer not found", HttpStatus.NOT_FOUND));

        if (transfer.getTransferType() != StockTransferType.NETWORK) {
            return; // Settlements are only for network transfers
        }

        if (settlementRepository.existsByTransferId(transferId)) {
            throw new BusinessException("SETTLEMENT_ALREADY_EXISTS", "A settlement for this transfer already exists", HttpStatus.BAD_REQUEST);
        }

        Shop sourceShop = transfer.getFromBranch().getShop();
        Shop destinationShop = transfer.getToBranch().getShop();

        if (sourceShop.getId().equals(destinationShop.getId())) {
            throw new BusinessException("SETTLEMENT_INVALID", "Cannot settle between the same shop", HttpStatus.BAD_REQUEST);
        }

        // Calculate gross amount from transfer items. 
        // For phase 19, we assume transfer cost is derived from the item's purchase price or standard pricing.
        // Assuming transfer.getItems() has the items. Let's just sum up a standard property.
        // Actually, for a B2B network, the requested amount or agreed value must exist. 
        // If not explicitly on StockTransfer, we might need to derive it from the InventoryItem's value.
        // Let's assume each inventory item has a `getSellingPrice()` or we use `getPurchaseTransaction().getPrice()`.
        // To be safe, we'll calculate a placeholder amount based on items, or throw if amount <= 0.
        BigDecimal totalAmount = transfer.getItems().stream()
                .map(item -> item.getInventoryItem().getSellingPrice() != null 
                             ? item.getInventoryItem().getSellingPrice() 
                             : BigDecimal.valueOf(10000)) // Fallback if no price defined
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("SETTLEMENT_AMOUNT_INVALID", "Settlement amount must be > 0", HttpStatus.BAD_REQUEST);
        }

        String settlementNumber = String.format("SET-%06d", settlementRepository.getNextSettlementNumberSequence());

        ShopSettlement settlement = ShopSettlement.builder()
                .settlementNumber(settlementNumber)
                .sourceShop(sourceShop)
                .destinationShop(destinationShop)
                .transfer(transfer)
                .grossAmount(totalAmount)
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(totalAmount)
                .status(SettlementStatus.PENDING)
                .build();

        settlementRepository.save(settlement);

        // Note: Audit log for system action (no user context necessarily if called asynchronously)
        UUID currentUserId = null;
        try {
            currentUserId = currentUserService.getCurrentUserId();
        } catch (Exception e) {
            // Ignore if no current user in context
        }

        auditService.logAction(currentUserId, destinationShop.getId(), 
                "SETTLEMENT_CREATED", "ShopSettlement", settlement.getId(), 
                null, null, null, "Settlement " + settlementNumber + " created for transfer " + transfer.getTransferNumber());
    }
}
