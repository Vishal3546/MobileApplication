package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.ApiResponseDto
import com.mobile.app.data.remote.dto.MediaDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface MediaApi {
    @Multipart
    @POST("api/v1/media")
    suspend fun uploadMedia(
        @Part file: MultipartBody.Part,
        @Query("type") type: String,
        @Query("bucket") bucket: String? = "default"
    ): Response<ApiResponseDto<MediaDto>>

    @GET("api/v1/media/{id}")
    suspend fun downloadMedia(@Path("id") id: UUID): Response<ResponseBody>
}
