package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName

enum class PaymentStatusDto {
    @SerializedName("PENDING") PENDING,
    @SerializedName("SUCCESS") SUCCESS,
    @SerializedName("FAILED") FAILED,
    @SerializedName("CANCELLED") CANCELLED
}
