package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.ApiResponseDto
import com.mobile.app.data.remote.dto.KycDocumentDto
import com.mobile.app.data.remote.dto.UploadKycRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface KycApi {
    @GET("api/v1/customers/{customerId}/documents")
    suspend fun getCustomerDocuments(
        @Path("customerId") customerId: UUID
    ): Response<ApiResponseDto<List<KycDocumentDto>>>

    @POST("api/v1/customers/{customerId}/documents")
    suspend fun uploadKyc(
        @Path("customerId") customerId: UUID,
        @Body request: UploadKycRequestDto
    ): Response<ApiResponseDto<KycDocumentDto>>

    @POST("api/v1/customers/{customerId}/documents/{documentId}/approve")
    suspend fun approveDocument(
        @Path("customerId") customerId: UUID,
        @Path("documentId") documentId: UUID,
        @Query("notes") notes: String? = null
    ): Response<ApiResponseDto<KycDocumentDto>>

    @POST("api/v1/customers/{customerId}/documents/{documentId}/reject")
    suspend fun rejectDocument(
        @Path("customerId") customerId: UUID,
        @Path("documentId") documentId: UUID,
        @Query("notes") notes: String? = null
    ): Response<ApiResponseDto<KycDocumentDto>>
}
