package com.mobile.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String, // Also acts as mobile based on backend contract
    val password: String
)

data class LoginResponse(
    @SerializedName("token")
    val accessToken: String,
    val refreshToken: String,
    val id: String?,
    val username: String?,
    val roles: List<String>?,
    val permissions: List<String>?
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String
)

data class LogoutResponse(
    val success: Boolean,
    val message: String? = null
)

data class UserDto(
    val id: String,
    val username: String,
    val roles: List<String>? = null,
    val permissions: List<String>? = null
)

data class ApiErrorResponse(
    val code: String?,
    val message: String?
)
