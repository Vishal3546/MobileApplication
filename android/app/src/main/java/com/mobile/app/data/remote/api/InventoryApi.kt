package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.inventory.InventoryResponse
import com.mobile.app.data.remote.dto.inventory.InventoryStatusHistoryResponse
import com.mobile.app.data.remote.dto.inventory.InventorySummaryResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.math.BigDecimal
import java.util.UUID

data class ChangeInventoryStatusRequest(val status: String, val reason: String?)
data class UpdateSellingPriceRequest(val sellingPrice: BigDecimal, val reason: String?)
data class ReserveInventoryRequest(val customerId: UUID?, val reason: String?)

interface InventoryApi {
    @GET("/api/v1/inventory")
    suspend fun getInventoryList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null,
        @Query("branchId") branchId: UUID? = null
    ): Response<com.mobile.app.data.remote.dto.PaginatedResponse<InventoryResponse>>

    @GET("/api/v1/inventory/{id}")
    suspend fun getInventoryById(@Path("id") id: UUID): Response<InventoryResponse>

    @GET("/api/v1/inventory/summary")
    suspend fun getInventorySummary(@Query("branchId") branchId: UUID? = null): Response<InventorySummaryResponse>

    @PATCH("/api/v1/inventory/{id}/status")
    suspend fun changeStatus(
        @Path("id") id: UUID,
        @Body request: ChangeInventoryStatusRequest
    ): Response<InventoryResponse>

    @PATCH("/api/v1/inventory/{id}/selling-price")
    suspend fun updateSellingPrice(
        @Path("id") id: UUID,
        @Body request: UpdateSellingPriceRequest
    ): Response<InventoryResponse>

    @POST("/api/v1/inventory/{id}/reserve")
    suspend fun reserveInventory(
        @Path("id") id: UUID,
        @Body request: ReserveInventoryRequest
    ): Response<InventoryResponse>

    @POST("/api/v1/inventory/{id}/release")
    suspend fun releaseInventory(@Path("id") id: UUID): Response<InventoryResponse>

    @GET("/api/v1/inventory/{id}/history")
    suspend fun getInventoryHistory(@Path("id") id: UUID): Response<List<InventoryStatusHistoryResponse>>
}
