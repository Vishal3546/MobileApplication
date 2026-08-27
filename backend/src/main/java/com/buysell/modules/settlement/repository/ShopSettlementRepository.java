package com.buysell.modules.settlement.repository;

import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.enums.SettlementStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ShopSettlementRepository extends JpaRepository<ShopSettlement, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShopSettlement s WHERE s.id = :id")
    Optional<ShopSettlement> findByIdWithLock(@Param("id") UUID id);

    boolean existsByTransferId(UUID transferId);

    Page<ShopSettlement> findBySourceShopIdOrDestinationShopId(UUID sourceShopId, UUID destinationShopId, Pageable pageable);

    Page<ShopSettlement> findBySourceShopIdOrDestinationShopIdAndStatus(UUID sourceShopId, UUID destinationShopId, SettlementStatus status, Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.remainingAmount), 0) FROM ShopSettlement s WHERE s.sourceShop.id = :shopId AND s.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')")
    BigDecimal sumReceivableForShop(@Param("shopId") UUID shopId);

    @Query("SELECT COALESCE(SUM(s.remainingAmount), 0) FROM ShopSettlement s WHERE s.destinationShop.id = :shopId AND s.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')")
    BigDecimal sumPayableForShop(@Param("shopId") UUID shopId);

    @Query(value = "SELECT nextval('settlement_number_seq')", nativeQuery = true)
    Long getNextSettlementNumberSequence();
}
