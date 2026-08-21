package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName

enum class PurchaseStatusDto {
    @SerializedName("INITIATED") INITIATED,
    @SerializedName("PENDING_KYC") PENDING_KYC,
    @SerializedName("PENDING_DEVICE_VERIFICATION") PENDING_DEVICE_VERIFICATION,
    @SerializedName("PENDING_INSPECTION") PENDING_INSPECTION,
    @SerializedName("PENDING_CONSENT") PENDING_CONSENT,
    @SerializedName("PENDING_PAYMENT") PENDING_PAYMENT,
    @SerializedName("COMPLETED") COMPLETED,
    @SerializedName("CANCELLED") CANCELLED
}
