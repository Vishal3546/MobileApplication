package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.ApiResponseDto
import com.mobile.app.data.remote.dto.CreateCustomerRequestDto
import com.mobile.app.data.remote.dto.CustomerDto
import com.mobile.app.data.remote.dto.PageResponseDto
import com.mobile.app.data.remote.dto.UpdateCustomerRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface CustomerApi {
    @GET("api/v1/customers")
    suspend fun getCustomers(
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponseDto<PageResponseDto<CustomerDto>>>

    @GET("api/v1/customers/{id}")
    suspend fun getCustomer(@Path("id") id: UUID): Response<ApiResponseDto<CustomerDto>>

    @POST("api/v1/customers")
    suspend fun createCustomer(@Body request: CreateCustomerRequestDto): Response<ApiResponseDto<CustomerDto>>

    @PUT("api/v1/customers/{id}")
    suspend fun updateCustomer(
        @Path("id") id: UUID,
        @Body request: UpdateCustomerRequestDto
    ): Response<ApiResponseDto<CustomerDto>>

    @PATCH("api/v1/customers/{id}/status")
    suspend fun updateCustomerStatus(
        @Path("id") id: UUID,
        @Query("status") status: String
    ): Response<ApiResponseDto<CustomerDto>>
}
