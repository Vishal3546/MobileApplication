package com.buysell.modules.sales.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.buysell.modules.sales.entity.SaleStatusHistory;

@Repository
public interface SaleStatusHistoryRepository extends JpaRepository<SaleStatusHistory, UUID> {
}
