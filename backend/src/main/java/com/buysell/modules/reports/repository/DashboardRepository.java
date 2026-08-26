package com.buysell.modules.reports.repository;

import com.buysell.modules.reports.dto.DashboardSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DashboardRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DashboardSummaryResponse getDashboardSummary(List<UUID> branchIds, ZonedDateTime currentStart, ZonedDateTime currentEnd, ZonedDateTime previousStart, ZonedDateTime previousEnd) {
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currentStart", currentStart.toOffsetDateTime())
                .addValue("currentEnd", currentEnd.toOffsetDateTime())
                .addValue("previousStart", previousStart.toOffsetDateTime())
                .addValue("previousEnd", previousEnd.toOffsetDateTime());
        
        String branchFilterSales = "";
        String branchFilterPurchases = "";
        String branchFilterInventory = "";
        String branchFilterCustomers = "";
        String branchFilterKyc = "";

        if (branchIds != null && !branchIds.isEmpty()) {
            params.addValue("branchIds", branchIds);
            branchFilterSales = " AND branch_id IN (:branchIds) ";
            branchFilterPurchases = " AND branch_id IN (:branchIds) ";
            branchFilterInventory = " AND branch_id IN (:branchIds) ";
            branchFilterCustomers = " AND branch_id IN (:branchIds) ";
            branchFilterKyc = " AND c.branch_id IN (:branchIds) ";
        }

        // 1. Current Period Sales & Profit
        String currentSalesSql = "SELECT COUNT(id) as count, COALESCE(SUM(final_amount), 0) as amount, " +
                "COALESCE(SUM(final_amount - (SELECT cost_price FROM inventory_items WHERE id = inventory_item_id)), 0) as margin " +
                "FROM sale_transactions WHERE sale_status = 'COMPLETED' " +
                "AND created_at >= :currentStart AND created_at < :currentEnd " + branchFilterSales;
        
        var currentSales = jdbcTemplate.queryForMap(currentSalesSql, params);

        // Previous Period Sales & Profit
        String prevSalesSql = "SELECT COUNT(id) as count, COALESCE(SUM(final_amount), 0) as amount, " +
                "COALESCE(SUM(final_amount - (SELECT cost_price FROM inventory_items WHERE id = inventory_item_id)), 0) as margin " +
                "FROM sale_transactions WHERE sale_status = 'COMPLETED' " +
                "AND created_at >= :previousStart AND created_at < :previousEnd " + branchFilterSales;
        
        var prevSales = jdbcTemplate.queryForMap(prevSalesSql, params);

        // 2. Current Period Purchases
        String currentPurchasesSql = "SELECT COUNT(id) as count, COALESCE(SUM(final_price), 0) as amount " +
                "FROM purchase_transactions WHERE transaction_status = 'COMPLETED' " +
                "AND created_at >= :currentStart AND created_at < :currentEnd " + branchFilterPurchases;
        
        var currentPurchases = jdbcTemplate.queryForMap(currentPurchasesSql, params);

        // Previous Period Purchases
        String prevPurchasesSql = "SELECT COUNT(id) as count, COALESCE(SUM(final_price), 0) as amount " +
                "FROM purchase_transactions WHERE transaction_status = 'COMPLETED' " +
                "AND created_at >= :previousStart AND created_at < :previousEnd " + branchFilterPurchases;
        
        var prevPurchases = jdbcTemplate.queryForMap(prevPurchasesSql, params);

        // 3. Inventory Stats (Current snapshot)
        String inventorySql = "SELECT " +
                "COALESCE(SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END), 0) as available_count, " +
                "COALESCE(SUM(CASE WHEN status = 'RESERVED' THEN 1 ELSE 0 END), 0) as reserved_count, " +
                "COALESCE(SUM(CASE WHEN status = 'IN_TRANSIT' THEN 1 ELSE 0 END), 0) as transit_count, " +
                "COALESCE(SUM(CASE WHEN status = 'SOLD' THEN 1 ELSE 0 END), 0) as sold_count " +
                "FROM inventory_items WHERE 1=1 " + branchFilterInventory;
        
        var invStats = jdbcTemplate.queryForMap(inventorySql, params);

        // 4. Pending / Exceptions (Current snapshot)
        String pendingSalesSql = "SELECT COUNT(id) FROM sale_transactions WHERE sale_status IN ('PENDING_PAYMENT', 'RESERVED') " + branchFilterSales;
        Long pendingPayments = jdbcTemplate.queryForObject(pendingSalesSql, params, Long.class);

        String customersSql = "SELECT COUNT(id) FROM customers WHERE created_at >= :currentStart AND created_at < :currentEnd " + branchFilterCustomers;
        Long customersAdded = jdbcTemplate.queryForObject(customersSql, params, Long.class);

        String activeBranchesSql = "SELECT COUNT(id) FROM branches WHERE is_active = true";
        Long activeBranches = jdbcTemplate.queryForObject(activeBranchesSql, params, Long.class);

        String pendingKycSql = "SELECT COUNT(cd.id) FROM customer_documents cd JOIN customers c ON cd.customer_id = c.id WHERE cd.verification_status = 'PENDING'" + branchFilterKyc;
        Long pendingKyc = jdbcTemplate.queryForObject(pendingKycSql, params, Long.class);

        return DashboardSummaryResponse.builder()
                .salesCount(buildMetric(((Number)currentSales.get("count")).longValue(), ((Number)prevSales.get("count")).longValue()))
                .salesAmount(buildMetric((BigDecimal)currentSales.get("amount"), (BigDecimal)prevSales.get("amount")))
                .grossProfit(buildMetric((BigDecimal)currentSales.get("margin"), (BigDecimal)prevSales.get("margin")))
                
                .purchasesCount(buildMetric(((Number)currentPurchases.get("count")).longValue(), ((Number)prevPurchases.get("count")).longValue()))
                .purchasesAmount(buildMetric((BigDecimal)currentPurchases.get("amount"), (BigDecimal)prevPurchases.get("amount")))
                
                .availableStockCount(((Number)invStats.getOrDefault("available_count", 0L)).longValue())
                .reservedStockCount(((Number)invStats.getOrDefault("reserved_count", 0L)).longValue())
                .inTransitStockCount(((Number)invStats.getOrDefault("transit_count", 0L)).longValue())
                .soldStockCount(((Number)invStats.getOrDefault("sold_count", 0L)).longValue())
                
                .pendingPayments(pendingPayments != null ? pendingPayments : 0L)
                .customersAdded(customersAdded != null ? customersAdded : 0L)
                .activeBranches(activeBranches != null ? activeBranches : 0L)
                .pendingKycCount(pendingKyc != null ? pendingKyc : 0L)
                .build();
    }

    private DashboardSummaryResponse.MetricWithComparison buildMetric(long current, long previous) {
        return buildMetric(BigDecimal.valueOf(current), BigDecimal.valueOf(previous));
    }

    private DashboardSummaryResponse.MetricWithComparison buildMetric(BigDecimal current, BigDecimal previous) {
        BigDecimal change = null;
        if (previous != null && previous.compareTo(BigDecimal.ZERO) != 0) {
            change = current.subtract(previous)
                    .divide(previous, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        return DashboardSummaryResponse.MetricWithComparison.builder()
                .currentPeriod(current)
                .previousPeriod(previous)
                .changePercentage(change)
                .build();
    }
}
