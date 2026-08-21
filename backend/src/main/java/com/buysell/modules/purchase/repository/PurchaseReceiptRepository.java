package com.buysell.modules.purchase.repository;

import com.buysell.modules.purchase.entity.PurchaseReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseReceiptRepository extends JpaRepository<PurchaseReceipt, UUID> {
    
    Optional<PurchaseReceipt> findByPurchaseTransactionId(UUID purchaseTransactionId);
}
