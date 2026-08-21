package com.mobile.app.data.remote.dto

data class PageResponseDto<T>(
    val content: List<T>?,
    val totalElements: Long,
    val totalPages: Int,
    val last: Boolean,
    val size: Int,
    val number: Int
)
