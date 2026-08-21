package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseTransitionDto(
    @SerializedName("status") val status: PurchaseStatusDto,
    @SerializedName("notes") val notes: String?
)
