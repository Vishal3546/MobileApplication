package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.purchase.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PurchaseApi {
    @POST("api/v1/purchases")
    suspend fun createPurchase(@Body request: PurchaseCreateDto): PurchaseDto

    @GET("api/v1/purchases/{id}")
    suspend fun getPurchase(@Path("id") id: String): PurchaseDto

    @GET("api/v1/purchases")
    suspend fun getPurchases(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): PurchaseListResponseDto

    @POST("api/v1/purchases/{id}/transition")
    suspend fun transitionPurchase(
        @Path("id") id: String,
        @Body request: PurchaseTransitionDto
    ): PurchaseDto

    @POST("api/v1/purchases/{id}/payments")
    suspend fun createPayment(
        @Path("id") id: String,
        @Body request: PurchasePaymentCreateDto
    ): PurchasePaymentDto

    @GET("api/v1/purchases/{id}/payments")
    suspend fun getPayments(@Path("id") id: String): List<PurchasePaymentDto>

    @POST("api/v1/purchases/{id}/complete")
    suspend fun completePurchase(@Path("id") id: String): PurchaseDto

    @POST("api/v1/purchases/{id}/cancel")
    suspend fun cancelPurchase(
        @Path("id") id: String,
        @Body request: PurchaseCancelDto
    ): PurchaseDto

    @GET("api/v1/purchases/{id}/receipt")
    suspend fun getReceipt(@Path("id") id: String): Response<ResponseBody>
}
