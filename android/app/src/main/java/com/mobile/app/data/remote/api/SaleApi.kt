package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.sales.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface SaleApi {
    @GET("/api/v1/sales")
    suspend fun getSales(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: String? = null,
        @Query("paymentStatus") paymentStatus: String? = null,
        @Query("search") search: String? = null
    ): Response<com.mobile.app.data.remote.dto.PaginatedResponse<SaleTransactionResponse>>

    @GET("/api/v1/sales/{id}")
    suspend fun getSaleById(@Path("id") id: UUID): Response<SaleTransactionResponse>

    @POST("/api/v1/sales")
    suspend fun createSale(@Body request: CreateSaleRequest): Response<com.mobile.app.data.remote.dto.ApiResponse<SaleTransactionResponse>>

    @POST("/api/v1/sales/{id}/override-price")
    suspend fun overridePrice(
        @Path("id") id: UUID,
        @Body request: OverrideSalePriceRequest
    ): Response<com.mobile.app.data.remote.dto.ApiResponse<SaleTransactionResponse>>

    @POST("/api/v1/sales/{id}/transition")
    suspend fun transitionSale(
        @Path("id") id: UUID,
        @Query("status") status: String
    ): Response<com.mobile.app.data.remote.dto.ApiResponse<SaleTransactionResponse>>

    @POST("/api/v1/sales/payments")
    suspend fun createPayment(@Body request: CreateSalePaymentRequest): Response<com.mobile.app.data.remote.dto.ApiResponse<SalePaymentResponse>>

    @GET("/api/v1/sales/{id}/payments")
    suspend fun getPaymentsForSale(@Path("id") id: UUID): Response<com.mobile.app.data.remote.dto.ApiResponse<List<SalePaymentResponse>>>

    @POST("/api/v1/sales/{id}/complete")
    suspend fun completeSale(@Path("id") id: UUID): Response<com.mobile.app.data.remote.dto.ApiResponse<SaleTransactionResponse>>

    @POST("/api/v1/sales/{id}/cancel")
    suspend fun cancelSale(
        @Path("id") id: UUID,
        @Body reason: String
    ): Response<com.mobile.app.data.remote.dto.ApiResponse<SaleTransactionResponse>>

    @GET("/api/v1/sales/{id}/invoice")
    suspend fun getSaleInvoice(@Path("id") id: UUID): Response<ResponseBody>
}
