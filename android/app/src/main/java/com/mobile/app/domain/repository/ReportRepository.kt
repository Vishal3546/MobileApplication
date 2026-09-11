package com.mobile.app.domain.repository

import com.mobile.app.domain.model.NetworkState
import com.mobile.app.data.remote.dto.reports.SalesReportResponse
import com.mobile.app.data.remote.dto.reports.PurchaseReportResponse
import com.mobile.app.data.remote.dto.reports.InventoryReportResponse
import java.util.UUID

interface ReportRepository {
    suspend fun getSalesReport(branchId: UUID?, dateRange: String?): NetworkState<SalesReportResponse>
    suspend fun getPurchaseReport(branchId: UUID?, dateRange: String?): NetworkState<PurchaseReportResponse>
    suspend fun getInventoryReport(branchId: UUID?): NetworkState<InventoryReportResponse>
}
