package com.buysell.modules.inventory.repository;

import com.buysell.modules.inventory.entity.StockTransfer;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface StockTransferRepository extends JpaRepository<StockTransfer, UUID>, JpaSpecificationExecutor<StockTransfer> {
    
    @Query(value = "SELECT nextval('stock_transfer_number_seq')", nativeQuery = true)
    Long getNextTransferNumberSequence();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM StockTransfer t WHERE t.id = :id")
    Optional<StockTransfer> findByIdWithLock(@Param("id") UUID id);
    
    Optional<StockTransfer> findByTransferNumber(String transferNumber);
}
