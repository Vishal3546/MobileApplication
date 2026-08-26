package com.buysell.modules.reports.repository;

import com.buysell.modules.reports.dto.ReportDTOs.*;
import com.buysell.modules.reports.dto.SalesReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SalesReportResponse getSalesReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime());
        
        String branchFilter = branchIds != null ? " AND branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String sql = "SELECT " +
                "COUNT(id) as sales_count, " +
                "COALESCE(SUM(selling_price), 0) as gross_sales, " +
                "COALESCE(SUM(discount_amount), 0) as discounts, " +
                "COALESCE(SUM(tax_amount), 0) as tax, " +
                "COALESCE(SUM(final_amount), 0) as net_sales, " +
                "COALESCE(SUM((SELECT cost_price FROM inventory_items WHERE id = inventory_item_id)), 0) as cost_value, " +
                "COALESCE(SUM(final_amount - (SELECT cost_price FROM inventory_items WHERE id = inventory_item_id)), 0) as gross_profit, " +
                "COALESCE(AVG(final_amount), 0) as avg_sale_value " +
                "FROM sale_transactions WHERE sale_status = 'COMPLETED' " +
                "AND created_at >= :start AND created_at < :end " + branchFilter;

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> 
            SalesReportResponse.builder()
                .salesCount(rs.getLong("sales_count"))
                .grossSales(rs.getBigDecimal("gross_sales"))
                .discounts(rs.getBigDecimal("discounts"))
                .tax(rs.getBigDecimal("tax"))
                .netSales(rs.getBigDecimal("net_sales"))
                .costValue(rs.getBigDecimal("cost_value"))
                .grossProfit(rs.getBigDecimal("gross_profit"))
                .averageSaleValue(rs.getBigDecimal("avg_sale_value"))
                .build()
        );
    }

    public PurchaseReportResponse getPurchaseReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime());
        
        String branchFilter = branchIds != null ? " AND branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String sql = "SELECT " +
                "COUNT(id) as purchase_count, " +
                "COALESCE(SUM(final_price), 0) as total_purchase_value, " +
                "COALESCE(AVG(final_price), 0) as avg_purchase_price, " +
                "SUM(CASE WHEN transaction_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_purchases, " +
                "SUM(CASE WHEN transaction_status = 'CANCELLED' THEN 1 ELSE 0 END) as cancelled_purchases " +
                "FROM purchase_transactions " +
                "WHERE created_at >= :start AND created_at < :end " + branchFilter;

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> 
            PurchaseReportResponse.builder()
                .purchaseCount(rs.getLong("purchase_count"))
                .totalPurchaseValue(rs.getBigDecimal("total_purchase_value"))
                .averagePurchasePrice(rs.getBigDecimal("avg_purchase_price"))
                .completedPurchases(rs.getLong("completed_purchases"))
                .cancelledPurchases(rs.getLong("cancelled_purchases"))
                .build()
        );
    }

    public InventoryReportResponse getInventoryReport(java.util.List<UUID> branchIds) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        String branchFilter = branchIds != null ? " AND branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String sql = "SELECT " +
                "COUNT(id) as total_inventory, " +
                "SUM(CASE WHEN status = 'AVAILABLE' THEN 1 ELSE 0 END) as available, " +
                "SUM(CASE WHEN status = 'RESERVED' THEN 1 ELSE 0 END) as reserved, " +
                "SUM(CASE WHEN status = 'IN_TRANSIT' THEN 1 ELSE 0 END) as in_transit, " +
                "SUM(CASE WHEN status = 'SOLD' THEN 1 ELSE 0 END) as sold, " +
                "SUM(CASE WHEN status = 'RETURNED' THEN 1 ELSE 0 END) as returned, " +
                "SUM(CASE WHEN status = 'DAMAGED' THEN 1 ELSE 0 END) as damaged, " +
                "SUM(CASE WHEN status = 'BLOCKED' THEN 1 ELSE 0 END) as blocked, " +
                "COALESCE(SUM(cost_price), 0) as total_acquisition_value, " +
                "COALESCE(SUM(CASE WHEN status IN ('AVAILABLE', 'RESERVED', 'IN_TRANSIT') THEN cost_price ELSE 0 END), 0) as available_stock_value, " +
                "COALESCE(SUM(CASE WHEN status IN ('AVAILABLE', 'RESERVED', 'IN_TRANSIT') THEN selling_price ELSE 0 END), 0) as total_listed_selling_value, " +
                "COALESCE(SUM(CASE WHEN status IN ('AVAILABLE', 'RESERVED', 'IN_TRANSIT') THEN selling_price - cost_price ELSE 0 END), 0) as unrealized_stock_margin " +
                "FROM inventory_items WHERE 1=1 " + branchFilter;

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> 
            InventoryReportResponse.builder()
                .totalInventory(rs.getLong("total_inventory"))
                .available(rs.getLong("available"))
                .reserved(rs.getLong("reserved"))
                .inTransit(rs.getLong("in_transit"))
                .sold(rs.getLong("sold"))
                .returned(rs.getLong("returned"))
                .damaged(rs.getLong("damaged"))
                .blocked(rs.getLong("blocked"))
                .totalAcquisitionValue(rs.getBigDecimal("total_acquisition_value"))
                .availableStockValue(rs.getBigDecimal("available_stock_value"))
                .totalListedSellingValue(rs.getBigDecimal("total_listed_selling_value"))
                .unrealizedStockMargin(rs.getBigDecimal("unrealized_stock_margin"))
                .build()
        );
    }

    private String buildSortClause(Pageable pageable, java.util.List<String> allowedSortFields) {
        if (pageable.getSort().isUnsorted()) {
            return " ORDER BY created_at DESC ";
        }
        StringBuilder sortClause = new StringBuilder(" ORDER BY ");
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            if (allowedSortFields.contains(property)) {
                sortClause.append(property)
                        .append(order.isAscending() ? " ASC " : " DESC ")
                        .append(", ");
            }
        }
        if (sortClause.toString().equals(" ORDER BY ")) {
            return " ORDER BY created_at DESC ";
        }
        return sortClause.substring(0, sortClause.length() - 2) + " ";
    }

    public Page<SaleReportDetail> getSalesDetailedReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime())
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        String branchFilter = branchIds != null ? " AND st.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String baseQuery = "FROM sale_transactions st " +
                "JOIN inventory_items ii ON st.inventory_item_id = ii.id " +
                "JOIN devices d ON ii.device_id = d.id " +
                "WHERE st.created_at >= :start AND st.created_at < :end " + branchFilter;

        String countQuery = "SELECT COUNT(st.id) " + baseQuery;
        Long total = jdbcTemplate.queryForObject(countQuery, params, Long.class);

        if (total == null || total == 0) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }

        String sortClause = buildSortClause(pageable, java.util.Arrays.asList("created_at", "final_amount", "selling_price"));
        
        String query = "SELECT st.id, st.sale_number, st.created_at, st.sale_status, st.selling_price, st.final_amount, " +
                "d.brand, d.model, (st.final_amount - ii.cost_price) as gross_profit " +
                baseQuery + sortClause + " LIMIT :limit OFFSET :offset";

        java.util.List<SaleReportDetail> content = jdbcTemplate.query(query, params, (rs, rowNum) -> 
            SaleReportDetail.builder()
                .id(UUID.fromString(rs.getString("id")))
                .saleNumber(rs.getString("sale_number"))
                .createdAt(rs.getObject("created_at", ZonedDateTime.class))
                .deviceBrand(rs.getString("brand"))
                .deviceModel(rs.getString("model"))
                .saleStatus(rs.getString("sale_status"))
                .sellingPrice(rs.getBigDecimal("selling_price"))
                .finalAmount(rs.getBigDecimal("final_amount"))
                .grossProfit(rs.getBigDecimal("gross_profit"))
                .build()
        );

        return new PageImpl<>(content, pageable, total);
    }

    public Page<PurchaseReportDetail> getPurchaseDetailedReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime())
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        String branchFilter = branchIds != null ? " AND pt.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String baseQuery = "FROM purchase_transactions pt " +
                "JOIN devices d ON pt.device_id = d.id " +
                "WHERE pt.created_at >= :start AND pt.created_at < :end " + branchFilter;

        String countQuery = "SELECT COUNT(pt.id) " + baseQuery;
        Long total = jdbcTemplate.queryForObject(countQuery, params, Long.class);

        if (total == null || total == 0) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }

        String sortClause = buildSortClause(pageable, java.util.Arrays.asList("created_at", "final_price"));
        
        String query = "SELECT pt.id, pt.purchase_number, pt.created_at, pt.transaction_status, pt.final_price, " +
                "d.brand, d.model " +
                baseQuery + sortClause + " LIMIT :limit OFFSET :offset";

        java.util.List<PurchaseReportDetail> content = jdbcTemplate.query(query, params, (rs, rowNum) -> 
            PurchaseReportDetail.builder()
                .id(UUID.fromString(rs.getString("id")))
                .transactionNumber(rs.getString("purchase_number"))
                .createdAt(rs.getObject("created_at", ZonedDateTime.class))
                .deviceBrand(rs.getString("brand"))
                .deviceModel(rs.getString("model"))
                .status(rs.getString("transaction_status"))
                .finalPrice(rs.getBigDecimal("final_price"))
                .build()
        );

        return new PageImpl<>(content, pageable, total);
    }

    public Page<InventoryReportDetail> getInventoryDetailedReport(java.util.List<UUID> branchIds, Pageable pageable) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", pageable.getPageSize())
                .addValue("offset", pageable.getOffset());

        String branchFilter = branchIds != null ? " AND ii.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String baseQuery = "FROM inventory_items ii " +
                "JOIN devices d ON ii.device_id = d.id " +
                "WHERE 1=1 " + branchFilter;

        String countQuery = "SELECT COUNT(ii.id) " + baseQuery;
        Long total = jdbcTemplate.queryForObject(countQuery, params, Long.class);

        if (total == null || total == 0) {
            return new PageImpl<>(java.util.Collections.emptyList(), pageable, 0);
        }

        String sortClause = buildSortClause(pageable, java.util.Arrays.asList("created_at", "cost_price", "selling_price"));
        
        String query = "SELECT ii.id, ii.stock_code, ii.created_at, ii.status, ii.cost_price, ii.selling_price, " +
                "d.brand, d.model " +
                baseQuery + sortClause + " LIMIT :limit OFFSET :offset";

        java.util.List<InventoryReportDetail> content = jdbcTemplate.query(query, params, (rs, rowNum) -> 
            InventoryReportDetail.builder()
                .id(UUID.fromString(rs.getString("id")))
                .stockCode(rs.getString("stock_code"))
                .createdAt(rs.getObject("created_at", ZonedDateTime.class))
                .deviceBrand(rs.getString("brand"))
                .deviceModel(rs.getString("model"))
                .status(rs.getString("status"))
                .costPrice(rs.getBigDecimal("cost_price"))
                .sellingPrice(rs.getBigDecimal("selling_price"))
                .build()
        );

        return new PageImpl<>(content, pageable, total);
    }

    public void streamSalesDetailedReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end, com.buysell.modules.reports.service.ReportExportService exportService, jakarta.servlet.http.HttpServletResponse response) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime());

        String branchFilter = branchIds != null ? " AND st.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String query = "SELECT st.id as \"ID\", st.sale_number as \"Sale Number\", st.created_at as \"Date\", " +
                "d.brand as \"Brand\", d.model as \"Model\", st.sale_status as \"Status\", " +
                "st.selling_price as \"Selling Price\", st.final_amount as \"Final Amount\", " +
                "(st.final_amount - ii.cost_price) as \"Gross Profit\" " +
                "FROM sale_transactions st " +
                "JOIN inventory_items ii ON st.inventory_item_id = ii.id " +
                "JOIN devices d ON ii.device_id = d.id " +
                "WHERE st.created_at >= :start AND st.created_at < :end " + branchFilter +
                "ORDER BY st.created_at DESC";

        exportService.exportToCsv(query, params, response, "sales_report");
    }

    public void streamPurchaseDetailedReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end, com.buysell.modules.reports.service.ReportExportService exportService, jakarta.servlet.http.HttpServletResponse response) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime());

        String branchFilter = branchIds != null ? " AND pt.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String query = "SELECT pt.id as \"ID\", pt.purchase_number as \"Purchase Number\", pt.created_at as \"Date\", " +
                "d.brand as \"Brand\", d.model as \"Model\", pt.transaction_status as \"Status\", " +
                "pt.final_price as \"Final Price\" " +
                "FROM purchase_transactions pt " +
                "JOIN devices d ON pt.device_id = d.id " +
                "WHERE pt.created_at >= :start AND pt.created_at < :end " + branchFilter +
                "ORDER BY pt.created_at DESC";

        exportService.exportToCsv(query, params, response, "purchase_report");
    }

    public void streamInventoryDetailedReport(java.util.List<UUID> branchIds, com.buysell.modules.reports.service.ReportExportService exportService, jakarta.servlet.http.HttpServletResponse response) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        String branchFilter = branchIds != null ? " AND ii.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String query = "SELECT ii.id as \"ID\", ii.stock_code as \"Stock Code\", ii.created_at as \"Date\", " +
                "d.brand as \"Brand\", d.model as \"Model\", ii.status as \"Status\", " +
                "ii.cost_price as \"Cost Price\", ii.selling_price as \"Selling Price\" " +
                "FROM inventory_items ii " +
                "JOIN devices d ON ii.device_id = d.id " +
                "WHERE 1=1 " + branchFilter +
                "ORDER BY ii.created_at DESC";

        exportService.exportToCsv(query, params, response, "inventory_report");
    }

    public java.util.List<EmployeePerformanceResponse> getEmployeeReport(java.util.List<UUID> branchIds, ZonedDateTime start, ZonedDateTime end) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("start", start.toOffsetDateTime())
                .addValue("end", end.toOffsetDateTime());

        String branchFilterS = branchIds != null ? " AND st.branch_id IN (:branchIds) " : "";
        String branchFilterP = branchIds != null ? " AND pt.branch_id IN (:branchIds) " : "";
        if (branchIds != null && !branchIds.isEmpty()) params.addValue("branchIds", branchIds);

        String sql = "SELECT e.first_name || ' ' || e.last_name as employee, " +
                "COALESCE(p.purchase_count, 0) as purchase_count, " +
                "COALESCE(p.purchase_value, 0) as purchase_value, " +
                "COALESCE(s.sales_count, 0) as sales_count, " +
                "COALESCE(s.sales_value, 0) as sales_value, " +
                "COALESCE(s.gross_profit, 0) as gross_profit " +
                "FROM employee_profiles e " +
                "LEFT JOIN (" +
                "   SELECT employee_id, COUNT(id) as purchase_count, SUM(final_price) as purchase_value " +
                "   FROM purchase_transactions pt WHERE transaction_status = 'COMPLETED' AND created_at >= :start AND created_at < :end " + branchFilterP +
                "   GROUP BY employee_id" +
                ") p ON e.user_id = p.employee_id " +
                "LEFT JOIN (" +
                "   SELECT st.employee_id, COUNT(st.id) as sales_count, SUM(st.final_amount) as sales_value, " +
                "   SUM(st.final_amount - (SELECT cost_price FROM inventory_items WHERE id = st.inventory_item_id)) as gross_profit " +
                "   FROM sale_transactions st WHERE st.sale_status = 'COMPLETED' AND st.created_at >= :start AND st.created_at < :end " + branchFilterS +
                "   GROUP BY st.employee_id" +
                ") s ON e.user_id = s.employee_id " +
                "WHERE (p.purchase_count > 0 OR s.sales_count > 0) " +
                "ORDER BY s.sales_value DESC NULLS LAST";

        return jdbcTemplate.query(sql, params, (rs, rowNum) -> 
            EmployeePerformanceResponse.builder()
                .employee(rs.getString("employee"))
                .purchaseCount(rs.getLong("purchase_count"))
                .purchaseValue(rs.getBigDecimal("purchase_value"))
                .salesCount(rs.getLong("sales_count"))
                .salesValue(rs.getBigDecimal("sales_value"))
                .grossProfit(rs.getBigDecimal("gross_profit"))
                .build()
        );
    }
}
