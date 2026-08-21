package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.inventory.StockTransferRequest
import com.mobile.app.data.remote.dto.inventory.StockTransferResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface StockTransferApi {
    @GET("/api/v1/inventory/transfers")
    suspend fun getTransfers(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: String? = null,
        @Query("branchId") branchId: UUID? = null
    ): Response<com.mobile.app.data.remote.dto.PaginatedResponse<StockTransferResponse>>

    @POST("/api/v1/inventory/transfers")
    suspend fun createTransfer(@Body request: StockTransferRequest): Response<StockTransferResponse>

    @GET("/api/v1/inventory/transfers/{id}")
    suspend fun getTransferById(@Path("id") id: UUID): Response<StockTransferResponse>

    @POST("/api/v1/inventory/transfers/{id}/transition")
    suspend fun transitionTransfer(
        @Path("id") id: UUID,
        @Query("status") status: String
    ): Response<StockTransferResponse>

    @POST("/api/v1/inventory/transfers/{id}/complete")
    suspend fun completeTransfer(@Path("id") id: UUID): Response<StockTransferResponse>
}
