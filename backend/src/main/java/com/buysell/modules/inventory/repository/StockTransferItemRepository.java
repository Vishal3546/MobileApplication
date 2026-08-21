package com.buysell.modules.inventory.repository;

import com.buysell.modules.inventory.entity.StockTransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StockTransferItemRepository extends JpaRepository<StockTransferItem, UUID> {
    boolean existsByInventoryItemIdAndStockTransferStatusIn(UUID inventoryItemId, java.util.Collection<com.buysell.modules.inventory.enums.TransferStatus> statuses);
}
