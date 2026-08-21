package com.buysell.modules.reports.controller;

import com.buysell.modules.reports.dto.ReportDTOs.*;
import com.buysell.modules.reports.dto.SalesReportResponse;
import com.buysell.modules.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_SALES_REPORT')")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return ResponseEntity.ok(reportService.getSalesReport(branchId, dateRange, startDate, endDate));
    }

    @GetMapping("/purchases")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_PURCHASE_REPORT')")
    public ResponseEntity<PurchaseReportResponse> getPurchaseReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return ResponseEntity.ok(reportService.getPurchaseReport(branchId, dateRange, startDate, endDate));
    }

    @GetMapping("/inventory")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_INVENTORY_REPORT')")
    public ResponseEntity<InventoryReportResponse> getInventoryReport(
            @RequestParam(required = false) UUID branchId
    ) {
        return ResponseEntity.ok(reportService.getInventoryReport(branchId));
    }

    private Pageable capPageSize(Pageable pageable) {
        if (pageable.getPageSize() > 500) {
            return PageRequest.of(pageable.getPageNumber(), 500, pageable.getSort());
        }
        return pageable;
    }

    @GetMapping("/sales/detailed")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_SALES_REPORT')")
    public ResponseEntity<Page<SaleReportDetail>> getSalesDetailedReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getSalesDetailedReport(branchId, dateRange, startDate, endDate, capPageSize(pageable)));
    }

    @GetMapping("/purchases/detailed")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_PURCHASE_REPORT')")
    public ResponseEntity<Page<PurchaseReportDetail>> getPurchaseDetailedReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getPurchaseDetailedReport(branchId, dateRange, startDate, endDate, capPageSize(pageable)));
    }

    @GetMapping("/inventory/detailed")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_INVENTORY_REPORT')")
    public ResponseEntity<Page<InventoryReportDetail>> getInventoryDetailedReport(
            @RequestParam(required = false) UUID branchId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(reportService.getInventoryDetailedReport(branchId, capPageSize(pageable)));
    }

    @GetMapping("/sales/export")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_SALES_REPORT') and @currentUserService.hasPermission('EXPORT_REPORT')")
    public void exportSalesReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response
    ) {
        reportService.exportSalesReport(branchId, dateRange, startDate, endDate, response);
    }

    @GetMapping("/purchases/export")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_PURCHASE_REPORT') and @currentUserService.hasPermission('EXPORT_REPORT')")
    public void exportPurchaseReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletResponse response
    ) {
        reportService.exportPurchaseReport(branchId, dateRange, startDate, endDate, response);
    }

    @GetMapping("/inventory/export")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_INVENTORY_REPORT') and @currentUserService.hasPermission('EXPORT_REPORT')")
    public void exportInventoryReport(
            @RequestParam(required = false) UUID branchId,
            HttpServletResponse response
    ) {
        reportService.exportInventoryReport(branchId, response);
    }

    @GetMapping("/employee")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_EMPLOYEE_REPORT')")
    public ResponseEntity<java.util.List<EmployeePerformanceResponse>> getEmployeeReport(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "this_month") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return ResponseEntity.ok(reportService.getEmployeeReport(branchId, dateRange, startDate, endDate));
    }
}
