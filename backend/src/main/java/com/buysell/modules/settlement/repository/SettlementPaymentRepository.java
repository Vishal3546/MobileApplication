package com.buysell.modules.settlement.repository;

import com.buysell.modules.settlement.entity.SettlementPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface SettlementPaymentRepository extends JpaRepository<SettlementPayment, UUID> {
    Optional<SettlementPayment> findBySettlementIdAndIdempotencyKey(UUID settlementId, String idempotencyKey);
    List<SettlementPayment> findBySettlementIdOrderByCreatedAtDesc(UUID settlementId);
}
