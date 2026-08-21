package com.buysell.modules.purchase.repository;

import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransaction, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PurchaseTransaction p WHERE p.id = :id")
    Optional<PurchaseTransaction> findByIdWithLock(@Param("id") UUID id);

    @Query(value = "SELECT nextval('purchase_number_seq')", nativeQuery = true)
    Long getNextPurchaseNumberSequence();

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM PurchaseTransaction p WHERE p.device.id = :deviceId AND p.transactionStatus NOT IN (:terminalStatuses)")
    boolean hasActivePurchaseForDevice(@Param("deviceId") UUID deviceId, @Param("terminalStatuses") List<TransactionStatus> terminalStatuses);

    @Query("SELECT p FROM PurchaseTransaction p WHERE " +
           "(:branchId IS NULL OR p.branch.id = :branchId) AND " +
           "(:customerId IS NULL OR p.customer.id = :customerId) AND " +
           "(:status IS NULL OR p.transactionStatus = :status)")
    Page<PurchaseTransaction> searchPurchases(
            @Param("branchId") UUID branchId,
            @Param("customerId") UUID customerId,
            @Param("status") TransactionStatus status,
            Pageable pageable);
}
