package com.buysell.modules.reports.service;

import com.buysell.modules.reports.dto.DashboardSummaryResponse;
import com.buysell.modules.reports.repository.DashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final BusinessDateTimeService dateTimeService;
    private final ReportSecurityService securityService;

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "@reportSecurityService.buildSecureCacheKey('summary', #requestedBranchId, #dateRangeStr)")
    public DashboardSummaryResponse getDashboardSummary(UUID requestedBranchId, String dateRangeStr, String customStart, String customEnd) {
        
        List<UUID> branchIds = securityService.resolveBranchScope(requestedBranchId);
        
        BusinessDateTimeService.DateRange currentRange = dateTimeService.resolveDateRange(dateRangeStr, customStart, customEnd);
        
        // Calculate previous period for comparison
        long daysDiff = ChronoUnit.DAYS.between(currentRange.start, currentRange.end);
        ZonedDateTime previousStart = currentRange.start.minusDays(daysDiff);
        ZonedDateTime previousEnd = currentRange.start; // The start of current is the end of previous
        
        return dashboardRepository.getDashboardSummary(
                branchIds, 
                currentRange.start, 
                currentRange.end, 
                previousStart, 
                previousEnd
        );
    }
}
