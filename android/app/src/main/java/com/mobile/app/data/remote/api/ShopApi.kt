package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.shop.CreateShopRequestDto
import com.mobile.app.data.remote.dto.shop.ShopDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShopApi {
    @GET("/api/v1/shops")
    suspend fun getShops(): Response<com.mobile.app.data.remote.dto.ApiResponse<List<ShopDto>>>

    @POST("/api/v1/shops")
    suspend fun createShop(@Body request: CreateShopRequestDto): Response<com.mobile.app.data.remote.dto.ApiResponse<ShopDto>>

    @GET("/api/v1/shops/{id}")
    suspend fun getShopById(@Path("id") id: java.util.UUID): Response<com.mobile.app.data.remote.dto.ApiResponse<ShopDto>>
}
