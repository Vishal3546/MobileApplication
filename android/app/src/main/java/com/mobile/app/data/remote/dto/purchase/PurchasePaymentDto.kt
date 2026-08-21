package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PurchasePaymentDto(
    @SerializedName("id") val id: String,
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("referenceNumber") val referenceNumber: String?,
    @SerializedName("paymentMode") val paymentMode: PaymentModeDto,
    @SerializedName("status") val status: PaymentStatusDto,
    @SerializedName("idempotencyKey") val idempotencyKey: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)
