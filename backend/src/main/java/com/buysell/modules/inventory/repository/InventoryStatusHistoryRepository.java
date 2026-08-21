package com.buysell.modules.inventory.repository;

import com.buysell.modules.inventory.entity.InventoryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InventoryStatusHistoryRepository extends JpaRepository<InventoryStatusHistory, UUID> {
    List<InventoryStatusHistory> findByInventoryItemIdOrderByCreatedAtDesc(UUID inventoryItemId);
}
