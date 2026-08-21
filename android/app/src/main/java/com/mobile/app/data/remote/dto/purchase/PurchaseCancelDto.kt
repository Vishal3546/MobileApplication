package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName

data class PurchaseCancelDto(
    @SerializedName("reason") val reason: String
)
