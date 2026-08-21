package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName
import com.mobile.app.data.remote.dto.CustomerDto
import com.mobile.app.data.remote.dto.device.DeviceDto
import java.math.BigDecimal

data class PurchaseDto(
    @SerializedName("id") val id: String,
    @SerializedName("purchaseNumber") val purchaseNumber: String,
    @SerializedName("customerId") val customerId: String,
    @SerializedName("deviceId") val deviceId: String,
    @SerializedName("customer") val customer: CustomerDto?,
    @SerializedName("device") val device: DeviceDto?,
    @SerializedName("suggestedPrice") val suggestedPrice: BigDecimal,
    @SerializedName("negotiatedPrice") val negotiatedPrice: BigDecimal,
    @SerializedName("finalPrice") val finalPrice: BigDecimal,
    @SerializedName("notes") val notes: String?,
    @SerializedName("status") val status: PurchaseStatusDto,
    @SerializedName("branchId") val branchId: String,
    @SerializedName("employeeId") val employeeId: String,
    @SerializedName("createdBy") val createdBy: String,
    @SerializedName("consentId") val consentId: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("completedAt") val completedAt: String?
)
