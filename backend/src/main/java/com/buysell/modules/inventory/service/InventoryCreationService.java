package com.buysell.modules.inventory.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.entity.InventoryStatusHistory;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.inventory.repository.InventoryStatusHistoryRepository;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCreationService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStatusHistoryRepository historyRepository;
    private final AuditService auditService;

    @Transactional(propagation = Propagation.REQUIRED)
    public InventoryItem createInventoryFromPurchase(PurchaseTransaction purchase) {
        log.info("Attempting to create inventory for purchase: {}", purchase.getId());

        if (purchase.getTransactionStatus() != TransactionStatus.COMPLETED) {
            throw new BusinessException("PURCHASE_NOT_COMPLETED", "Purchase must be COMPLETED to create inventory.", HttpStatus.BAD_REQUEST);
        }

        // Idempotency check: Does inventory already exist for this purchase?
        Optional<InventoryItem> existingItem = inventoryItemRepository.findByPurchaseTransactionId(purchase.getId());
        if (existingItem.isPresent()) {
            log.info("Inventory already exists for purchase: {}", purchase.getId());
            return existingItem.get();
        }

        // Validate Device active inventory uniqueness
        // Assuming status AVAILABLE, RESERVED, BLOCKED, IN_TRANSIT are the active states
        if (inventoryItemRepository.hasActiveInventoryForDevice(
                purchase.getDevice().getId(), 
                java.util.List.of(InventoryStatus.AVAILABLE, InventoryStatus.RESERVED, InventoryStatus.BLOCKED, InventoryStatus.IN_TRANSIT))) {
            throw new BusinessException("INVENTORY_ALREADY_EXISTS", "Device already has an active inventory record.", HttpStatus.CONFLICT);
        }

        String stockCode = generateStockCode();

        InventoryItem item = InventoryItem.builder()
                .stockCode(stockCode)
                .device(purchase.getDevice())
                .purchaseTransaction(purchase)
                .branch(purchase.getBranch())
                .status(InventoryStatus.AVAILABLE)
                .costPrice(purchase.getFinalPrice())
                .createdBy(purchase.getEmployee())
                .build();

        item = inventoryItemRepository.save(item);

        InventoryStatusHistory history = InventoryStatusHistory.builder()
                .inventoryItem(item)
                .previousStatus(null)
                .newStatus(InventoryStatus.AVAILABLE)
                .reason("Inventory created from purchase")
                .referenceType("PURCHASE_TRANSACTION")
                .referenceId(purchase.getId())
                .performedBy(purchase.getEmployee())
                .branch(purchase.getBranch())
                .build();
        historyRepository.save(history);

        auditService.logAction(
                purchase.getEmployee().getId(),
                purchase.getBranch().getId(),
                "INVENTORY_CREATED",
                "InventoryItem",
                item.getId(),
                purchase.getId().toString(),
                purchase.getDevice().getId().toString(),
                null,
                "Inventory " + stockCode + " created."
        );

        return item;
    }

    private String generateStockCode() {
        Long sequence = inventoryItemRepository.getNextStockCodeSequence();
        return String.format("STK-%d-%06d", Year.now().getValue(), sequence);
    }
}
