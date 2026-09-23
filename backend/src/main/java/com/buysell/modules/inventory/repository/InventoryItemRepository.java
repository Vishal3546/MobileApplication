package com.buysell.modules.inventory.repository;

import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID>, JpaSpecificationExecutor<InventoryItem> {

    @Query(value = "SELECT nextval('inventory_stock_code_seq')", nativeQuery = true)
    Long getNextStockCodeSequence();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryItem i WHERE i.id = :id")
    Optional<InventoryItem> findByIdWithLock(@Param("id") UUID id);

    Optional<InventoryItem> findByStockCode(String stockCode);

    boolean existsByPurchaseTransactionId(UUID purchaseTransactionId);
    
    Optional<InventoryItem> findByPurchaseTransactionId(UUID purchaseTransactionId);

    @Query("SELECT COUNT(i) > 0 FROM InventoryItem i WHERE i.device.id = :deviceId AND i.status IN :statuses")
    boolean hasActiveInventoryForDevice(@Param("deviceId") UUID deviceId, @Param("statuses") java.util.Collection<InventoryStatus> statuses);

    @Query("SELECT i.device.brand as brand, COUNT(i) as count, COALESCE(SUM(i.sellingPrice), 0) as totalValue " +
           "FROM InventoryItem i " +
           "WHERE (cast(:status as text) IS NULL OR CAST(i.status as string) = :status) " +
           "AND (cast(:branchId as text) IS NULL OR i.branch.id = :branchId) " +
           "AND (cast(:search as text) IS NULL OR LOWER(i.device.brand) LIKE LOWER(CONCAT('%', cast(:search as text), '%')) " +
           "OR LOWER(i.device.model) LIKE LOWER(CONCAT('%', cast(:search as text), '%')) " +
           "OR LOWER(i.device.imei1) LIKE LOWER(CONCAT('%', cast(:search as text), '%'))) " +
           "GROUP BY i.device.brand " +
           "ORDER BY COUNT(i) DESC")
    java.util.List<com.buysell.modules.inventory.dto.BrandSummaryProjection> getBrandWiseSummary(
        @Param("status") String status,
        @Param("search") String search,
        @Param("branchId") UUID branchId
    );
}
