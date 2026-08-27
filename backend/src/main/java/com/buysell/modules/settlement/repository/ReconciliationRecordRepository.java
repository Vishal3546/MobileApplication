package com.buysell.modules.settlement.repository;

import com.buysell.modules.settlement.entity.ReconciliationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReconciliationRecordRepository extends JpaRepository<ReconciliationRecord, UUID> {
    Page<ReconciliationRecord> findByShopId(UUID shopId, Pageable pageable);
}
