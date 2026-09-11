package com.mobile.app.data.repository

import com.mobile.app.data.remote.api.ReportApi
import com.mobile.app.data.remote.dto.reports.SalesReportResponse
import com.mobile.app.data.remote.dto.reports.PurchaseReportResponse
import com.mobile.app.data.remote.dto.reports.InventoryReportResponse
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.safeApiCall
import com.mobile.app.domain.repository.ReportRepository
import java.util.UUID
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val api: ReportApi
) : ReportRepository {

    override suspend fun getSalesReport(branchId: UUID?, dateRange: String?): NetworkState<SalesReportResponse> {
        return safeApiCall { api.getSalesReport(branchId, dateRange) }
    }

    override suspend fun getPurchaseReport(branchId: UUID?, dateRange: String?): NetworkState<PurchaseReportResponse> {
        return safeApiCall { api.getPurchaseReport(branchId, dateRange) }
    }

    override suspend fun getInventoryReport(branchId: UUID?): NetworkState<InventoryReportResponse> {
        return safeApiCall { api.getInventoryReport(branchId) }
    }
}
