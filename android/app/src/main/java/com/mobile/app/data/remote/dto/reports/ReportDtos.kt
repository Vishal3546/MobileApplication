package com.mobile.app.data.remote.dto.reports

import java.math.BigDecimal

data class SalesReportResponse(
    val totalSales: BigDecimal,
    val totalProfit: BigDecimal,
    val saleCount: Int,
    val averageSaleValue: BigDecimal,
    val summaryByPaymentMode: Map<String, BigDecimal>
)

data class PurchaseReportResponse(
    val totalPurchases: BigDecimal,
    val purchaseCount: Int,
    val averagePurchaseValue: BigDecimal
)

data class InventoryReportResponse(
    val totalItems: Int,
    val totalValue: BigDecimal,
    val statusBreakdown: Map<String, Int>
)
