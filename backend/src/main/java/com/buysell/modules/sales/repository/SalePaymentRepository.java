package com.buysell.modules.sales.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buysell.modules.sales.entity.SalePayment;
import com.buysell.modules.sales.enums.PaymentStatus;

@Repository
public interface SalePaymentRepository extends JpaRepository<SalePayment, UUID> {
    Optional<SalePayment> findByIdempotencyKey(String idempotencyKey);
    List<SalePayment> findBySaleTransactionIdAndPaymentStatus(UUID saleTransactionId, PaymentStatus status);
    List<SalePayment> findBySaleTransactionId(UUID saleTransactionId);
}
