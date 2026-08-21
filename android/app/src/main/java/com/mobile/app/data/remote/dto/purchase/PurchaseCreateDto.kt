package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PurchaseCreateDto(
    @SerializedName("customerId") val customerId: String,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("suggestedPrice") val suggestedPrice: BigDecimal,
    @SerializedName("negotiatedPrice") val negotiatedPrice: BigDecimal,
    @SerializedName("finalPrice") val finalPrice: BigDecimal,
    @SerializedName("notes") val notes: String?
)
