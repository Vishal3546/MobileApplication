package com.buysell.modules.purchase.repository;

import com.buysell.modules.purchase.entity.PurchasePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchasePaymentRepository extends JpaRepository<PurchasePayment, UUID> {
    
    List<PurchasePayment> findByPurchaseTransactionIdOrderByCreatedAtDesc(UUID purchaseTransactionId);
    
    Optional<PurchasePayment> findByIdempotencyKey(String idempotencyKey);
}
