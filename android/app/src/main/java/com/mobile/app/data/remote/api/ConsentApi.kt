package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.ApiResponseDto
import com.mobile.app.data.remote.dto.CaptureConsentRequestDto
import com.mobile.app.data.remote.dto.ConsentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface ConsentApi {
    @GET("api/v1/customers/{customerId}/consents")
    suspend fun getCustomerConsents(
        @Path("customerId") customerId: UUID
    ): Response<ApiResponseDto<List<ConsentDto>>>

    @POST("api/v1/customers/{customerId}/consents")
    suspend fun captureConsent(
        @Path("customerId") customerId: UUID,
        @Body request: CaptureConsentRequestDto
    ): Response<ApiResponseDto<ConsentDto>>
}
