package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.reports.SalesReportResponse
import com.mobile.app.data.remote.dto.reports.PurchaseReportResponse
import com.mobile.app.data.remote.dto.reports.InventoryReportResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.UUID

interface ReportApi {
    @GET("api/v1/reports/sales")
    suspend fun getSalesReport(
        @Query("branchId") branchId: UUID? = null,
        @Query("dateRange") dateRange: String? = null, // "DAILY", "MONTHLY", "YEARLY"
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<SalesReportResponse>

    @GET("api/v1/reports/purchases")
    suspend fun getPurchaseReport(
        @Query("branchId") branchId: UUID? = null,
        @Query("dateRange") dateRange: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<PurchaseReportResponse>

    @GET("api/v1/reports/inventory")
    suspend fun getInventoryReport(
        @Query("branchId") branchId: UUID? = null
    ): Response<InventoryReportResponse>
}
