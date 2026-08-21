package com.mobile.app.data.remote.dto

data class ApiResponseDto<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)
