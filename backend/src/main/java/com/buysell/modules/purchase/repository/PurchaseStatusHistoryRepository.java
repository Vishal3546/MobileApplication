package com.buysell.modules.purchase.repository;

import com.buysell.modules.purchase.entity.PurchaseStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseStatusHistoryRepository extends JpaRepository<PurchaseStatusHistory, UUID> {
    
    List<PurchaseStatusHistory> findByPurchaseTransactionIdOrderByChangedAtAsc(UUID purchaseTransactionId);
}
