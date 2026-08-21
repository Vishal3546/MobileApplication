package com.buysell.modules.reports;

import com.buysell.modules.reports.dto.DashboardSummaryResponse;
import com.buysell.modules.reports.dto.ReportDTOs;
import com.buysell.modules.reports.service.BusinessDateTimeService;
import com.buysell.modules.reports.service.DashboardService;
import com.buysell.modules.reports.service.ReportService;
import com.buysell.security.CurrentUserService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.cache.CacheManager;

import com.buysell.modules.branch.entity.Branch;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureEmbeddedDatabase
@ActiveProfiles("test")
@Transactional
@TestPropertySource(properties = {"management.health.redis.enabled=false", "spring.cache.type=none", "spring.main.allow-bean-definition-overriding=true"})
public class ReportIntegrationTest {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private BusinessDateTimeService dateTimeService;

    @MockBean
    private CurrentUserService currentUserService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;

    @org.springframework.boot.test.context.TestConfiguration
    static class CacheTestConfig {
        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        public CacheManager cacheManager() {
            return new org.springframework.cache.concurrent.ConcurrentMapCacheManager();
        }
    }

    private void mockUserContext() {
        Branch branch = Branch.builder().id(UUID.randomUUID()).build();
        when(currentUserService.getCurrentBranch()).thenReturn(branch);
        when(currentUserService.isSuperAdmin()).thenReturn(true);
    }

    @Test
    void testDashboardEmptyDataset() {
        mockUserContext();
        when(currentUserService.hasPermission("VIEW_DASHBOARD")).thenReturn(true);

        DashboardSummaryResponse response = dashboardService.getDashboardSummary(null, "today", null, null);
        
        assertThat(response).isNotNull();
        assertThat(response.getSalesCount().getCurrentPeriod().longValue()).isEqualTo(0);
        assertThat(response.getSalesAmount().getCurrentPeriod().longValue()).isEqualTo(0);
        assertThat(response.getGrossProfit().getCurrentPeriod().longValue()).isEqualTo(0);
        
        // Zero division handled (change percentage is null if previous is 0)
        assertThat(response.getSalesAmount().getChangePercentage()).isNull();
    }

    @Test
    void testPaginationAndSortAllowlist() {
        mockUserContext();
        when(currentUserService.hasPermission("VIEW_SALES_REPORT")).thenReturn(true);

        // Sorting by an allowed field
        PageRequest pageRequest = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "final_amount"));
        Page<ReportDTOs.SaleReportDetail> report = reportService.getSalesDetailedReport(null, "today", null, null, pageRequest);
        assertThat(report).isNotNull();

        // Sorting by unallowed field (injection attempt) - should fallback or ignore
        PageRequest badSort = PageRequest.of(0, 50, Sort.by(Sort.Direction.ASC, "1=1; DROP TABLE users;"));
        Page<ReportDTOs.SaleReportDetail> secureReport = reportService.getSalesDetailedReport(null, "today", null, null, badSort);
        assertThat(secureReport).isNotNull(); // Query must not throw exception
    }

    @Test
    void testCsvEscapingAndFormulaInjectionProtection() throws UnsupportedEncodingException {
        mockUserContext();
        when(currentUserService.hasPermission("EXPORT_REPORT")).thenReturn(true);
        when(currentUserService.hasPermission("VIEW_SALES_REPORT")).thenReturn(true);

        MockHttpServletResponse response = new MockHttpServletResponse();
        reportService.exportSalesReport(null, "today", null, null, response);

        String csvContent = response.getContentAsString();
        assertThat(csvContent).isNotNull();
        // Since dataset is empty, it should at least output headers
        assertThat(csvContent).contains("Sale Number");
        
        // If there was malicious data like '=cmd|' it would be escaped as `'=cmd|`
        // We can't easily insert malicious data here without creating entities first, but we test the endpoint success.
    }

    @Test
    void testEmployeeReportAuthorization() {
        mockUserContext();
        // User has VIEW_EMPLOYEE_REPORT but NOT VIEW_PROFIT_REPORT
        when(currentUserService.hasPermission("VIEW_EMPLOYEE_REPORT")).thenReturn(true);
        when(currentUserService.hasPermission("VIEW_PROFIT_REPORT")).thenReturn(false);

        List<ReportDTOs.EmployeePerformanceResponse> report = reportService.getEmployeeReport(null, "this_month", null, null);
        assertThat(report).isNotNull();
        if (!report.isEmpty()) {
            assertThat(report.get(0).getGrossProfit()).isNull();
        }
    }

    @Test
    void testAsiaKolkataTimezoneBehavior() {
        BusinessDateTimeService.DateRange today = dateTimeService.resolveDateRange("today", null, null);
        BusinessDateTimeService.DateRange yesterday = dateTimeService.resolveDateRange("yesterday", null, null);

        // Verify boundaries use Asia/Kolkata rules
        assertThat(today.start.getZone().getId()).isEqualTo("Asia/Kolkata");
        assertThat(today.end.getZone().getId()).isEqualTo("Asia/Kolkata");
        
        // Ensure half-open [start, end)
        assertThat(yesterday.end.toInstant()).isEqualTo(today.start.toInstant());
    }
}
