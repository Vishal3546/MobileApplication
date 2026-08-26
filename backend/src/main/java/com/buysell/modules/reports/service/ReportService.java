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
import java.util.List;
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
    public SalesReportResponse getSalesReport(UUID requestedBranchId, String dateRangeStr, String customStart,
            String customEnd) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getSalesReport(branchIds, currentRange.start, currentRange.end);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "purchaseReport", key = "@reportSecurityService.buildSecureCacheKey('purchase', #requestedBranchId, #dateRangeStr)")
    public PurchaseReportResponse getPurchaseReport(UUID requestedBranchId, String dateRangeStr, String customStart,
            String customEnd) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getPurchaseReport(branchIds, currentRange.start, currentRange.end);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "inventoryReport", key = "@reportSecurityService.buildSecureCacheKey('inventory', #requestedBranchId, 'current')")
    public InventoryReportResponse getInventoryReport(UUID requestedBranchId) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        return reportRepository.getInventoryReport(branchIds);
    }

    @Transactional(readOnly = true)
    public Page<SaleReportDetail> getSalesDetailedReport(UUID requestedBranchId, String dateRangeStr,
            String customStart, String customEnd, Pageable pageable) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getSalesDetailedReport(branchIds, currentRange.start, currentRange.end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<PurchaseReportDetail> getPurchaseDetailedReport(UUID requestedBranchId, String dateRangeStr,
            String customStart, String customEnd, Pageable pageable) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        return reportRepository.getPurchaseDetailedReport(branchIds, currentRange.start, currentRange.end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<InventoryReportDetail> getInventoryDetailedReport(UUID requestedBranchId, Pageable pageable) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        return reportRepository.getInventoryDetailedReport(branchIds, pageable);
    }

    @Transactional(readOnly = true)
    public void exportSalesReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd,
            HttpServletResponse response) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        reportRepository.streamSalesDetailedReport(branchIds, currentRange.start, currentRange.end, exportService,
                response);
    }

    @Transactional(readOnly = true)
    public void exportPurchaseReport(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd,
            HttpServletResponse response) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        reportRepository.streamPurchaseDetailedReport(branchIds, currentRange.start, currentRange.end, exportService,
                response);
    }

    @Transactional(readOnly = true)
    public void exportInventoryReport(UUID requestedBranchId, HttpServletResponse response) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        reportRepository.streamInventoryDetailedReport(branchIds, exportService, response);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.buysell.modules.reports.dto.ReportDTOs.EmployeePerformanceResponse> getEmployeeReport(
            UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd) {
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        var currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        java.util.List<com.buysell.modules.reports.dto.ReportDTOs.EmployeePerformanceResponse> report = reportRepository
                .getEmployeeReport(branchIds, currentRange.start, currentRange.end);

        if (!securityService.hasPermission("VIEW_PROFIT_REPORT")) {
            report.forEach(r -> r.setGrossProfit(null));
        }
        return report;
    }

    @org.springframework.cache.annotation.CacheEvict(value = { "dashboard", "salesReport", "purchaseReport",
            "inventoryReport" }, allEntries = true)
    public void invalidateReportCache() {
        // Called via Spring Events or manually by transaction services (purchase
        // completion, sale completion, inventory changes, etc.)
    }
}
