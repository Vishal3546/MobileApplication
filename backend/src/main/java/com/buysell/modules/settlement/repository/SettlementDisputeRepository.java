package com.buysell.modules.settlement.repository;

import com.buysell.modules.settlement.entity.SettlementDispute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface SettlementDisputeRepository extends JpaRepository<SettlementDispute, UUID> {
    Page<SettlementDispute> findBySettlementSourceShopIdOrSettlementDestinationShopId(UUID sourceId, UUID destId, Pageable pageable);
    List<SettlementDispute> findBySettlementId(UUID settlementId);
}
