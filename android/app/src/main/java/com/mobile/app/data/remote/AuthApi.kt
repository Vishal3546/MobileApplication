package com.mobile.app.data.remote

import com.mobile.app.data.remote.dto.ApiResponseDto
import com.mobile.app.data.remote.dto.LoginRequest
import com.mobile.app.data.remote.dto.LoginResponse
import com.mobile.app.data.remote.dto.LogoutResponse
import com.mobile.app.data.remote.dto.RefreshTokenRequest
import com.mobile.app.data.remote.dto.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponseDto<LoginResponse>>

    @POST("api/v1/auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<ApiResponseDto<RefreshTokenResponse>>

    @POST("api/v1/auth/logout")
    suspend fun logout(): Response<LogoutResponse>
}
