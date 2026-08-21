package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PurchasePaymentCreateDto(
    @SerializedName("amount") val amount: BigDecimal,
    @SerializedName("referenceNumber") val referenceNumber: String?,
    @SerializedName("paymentMode") val paymentMode: PaymentModeDto,
    @SerializedName("idempotencyKey") val idempotencyKey: String
)
