package com.buysell.modules.reports.service;

import com.buysell.modules.reports.dto.ReportDTOs.*;
import com.buysell.modules.reports.dto.SalesReportResponse;
import com.buysell.modules.reports.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final BusinessDateTimeService dateTimeService;
    private final ReportSecurityService securityService;
    private final ReportExportService exportService;

    @Transactional(readOnly = true)
    @Cacheable(value = "salesReport", key = "@reportSecurityService.buildSecureCacheKey('sales', #requestedBranchId, #dateRangeStr)")
    public SalesReportResponse getSalesReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getSalesReport(branchId, currentRange.start, currentRange.end);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "purchaseReport", key = "@reportSecurityService.buildSecureCacheKey('purchase', #requestedBranchId, #dateRangeStr)")
    public PurchaseReportResponse getPurchaseReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getPurchaseReport(branchId, currentRange.start, currentRange.end);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "inventoryReport", key = "@reportSecurityService.buildSecureCacheKey('inventory', #requestedBranchId, 'current')")
    public InventoryReportResponse getInventoryReport(UUID requestedBranchId) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        return reportRepository.getInventoryReport(branchId);
    }

    @Transactional(readOnly = true)
    public Page<SaleReportDetail> getSalesDetailedReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd, Pageable pageable) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getSalesDetailedReport(branchId, currentRange.start, currentRange.end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseReportDetail> getPurchaseDetailedReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd, Pageable pageable) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getPurchaseDetailedReport(branchId, currentRange.start, currentRange.end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<InventoryReportDetail> getInventoryDetailedReport(UUID requestedBranchId, Pageable pageable) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        return reportRepository.getInventoryDetailedReport(branchId, pageable);
    }

    @Transactional(readOnly = true)
    public void exportSalesReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd, HttpServletResponse response) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        reportRepository.streamSalesDetailedReport(branchId, currentRange.start, currentRange.end, exportService, response);
    }

    @Transactional(readOnly = true)
    public void exportPurchaseReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd, HttpServletResponse response) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        reportRepository.streamPurchaseDetailedReport(branchId, currentRange.start, currentRange.end, exportService, response);
    }

    @Transactional(readOnly = true)
    public void exportInventoryReport(UUID requestedBranchId, HttpServletResponse response) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        reportRepository.streamInventoryDetailedReport(branchId, exportService, response);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.buysell.modules.reports.dto.ReportDTOs.EmployeePerformanceResponse> getEmployeeReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd) {
        UUID branchId = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        java.util.List<com.buysell.modules.reports.dto.ReportDTOs.EmployeePerformanceResponse> report = reportRepository.getEmployeeReport(branchId, currentRange.start, currentRange.end);
        
        if (!securityService.hasPermission("VIEW_PROFIT_REPORT")) {
            report.forEach(r -> r.setGrossProfit(null));
        }
        return report;
    }

    @org.springframework.cache.annotation.CacheEvict(value = {"dashboard", "salesReport", "purchaseReport", "inventoryReport"}, allEntries = true)
    public void invalidateReportCache() {
        // Called via Spring Events or manually by transaction services (purchase completion, sale completion, inventory changes, etc.)
    }
}
