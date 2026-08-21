package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseListResponseDto(
    @SerializedName("content") val content: List<PurchaseDto>,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("size") val size: Int
)
