package com.buysell.modules.reports.controller;

import com.buysell.modules.reports.dto.DashboardSummaryResponse;
import com.buysell.modules.reports.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("@currentUserService.hasPermission('VIEW_DASHBOARD')")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false, defaultValue = "today") String dateRange,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(branchId, dateRange, startDate, endDate));
    }
}
