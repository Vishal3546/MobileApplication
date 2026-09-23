package com.buysell.modules.reports.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportResponse {
    private long salesCount;
    private BigDecimal grossSales;
    private BigDecimal discounts;
    private BigDecimal tax;
    private BigDecimal netSales;
    private BigDecimal costValue;
    private BigDecimal grossProfit;
    private BigDecimal averageSaleValue;
}
