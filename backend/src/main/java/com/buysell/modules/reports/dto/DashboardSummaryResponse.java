package com.buysell.modules.reports.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardSummaryResponse {

    private MetricWithComparison purchasesCount;
    private MetricWithComparison purchasesAmount;
    
    private MetricWithComparison salesCount;
    private MetricWithComparison salesAmount;
    
    private MetricWithComparison grossProfit;

    private long availableStockCount;
    private long reservedStockCount;
    private long inTransitStockCount;
    private long soldStockCount;

    private long pendingPayments;
    private long customersAdded;
    private long activeBranches;
    private long pendingKycCount;

    @Data
    @Builder
    public static class MetricWithComparison {
        private BigDecimal currentPeriod;
        private BigDecimal previousPeriod;
        private BigDecimal changePercentage; // null if previousPeriod is 0
    }
}
