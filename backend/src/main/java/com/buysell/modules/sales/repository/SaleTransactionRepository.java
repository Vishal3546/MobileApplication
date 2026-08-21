package com.buysell.modules.sales.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.buysell.modules.sales.entity.SaleTransaction;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, UUID> {
    Optional<SaleTransaction> findBySaleNumber(String saleNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SaleTransaction s WHERE s.id = :id")
    Optional<SaleTransaction> findByIdWithLock(UUID id);

    @Query(value = "SELECT CONCAT('SALE-', TO_CHAR(CURRENT_DATE, 'YYYY'), '-', LPAD(NEXTVAL('sale_number_seq')::TEXT, 6, '0'))", nativeQuery = true)
    String generateSaleNumber();
}
