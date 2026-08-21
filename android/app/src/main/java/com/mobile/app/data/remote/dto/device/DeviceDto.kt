package com.mobile.app.data.remote.dto.device

import com.google.gson.annotations.SerializedName



data class DeviceDto(
    @SerializedName( "id") val id: String,
    @SerializedName( "brand") val brand: String,
    @SerializedName( "model") val model: String,
    @SerializedName( "variant") val variant: String?,
    @SerializedName( "color") val color: String,
    @SerializedName( "storage") val storage: String,
    @SerializedName( "ram") val ram: String,
    @SerializedName( "imei1") val imei1: String,
    @SerializedName( "imei2") val imei2: String?,
    @SerializedName( "serialNumber") val serialNumber: String?,
    @SerializedName( "status") val status: String, // ACTIVE, BLOCKED
    @SerializedName( "branchId") val branchId: String,
    @SerializedName( "createdBy") val createdBy: String,
    @SerializedName( "updatedBy") val updatedBy: String?,
    @SerializedName( "createdAt") val createdAt: String,
    @SerializedName( "updatedAt") val updatedAt: String,
    @SerializedName( "verificationState") val verificationState: String,
    @SerializedName( "mediaCount") val mediaCount: Int,
    @SerializedName( "latestCondition") val latestCondition: DeviceConditionDto?,
    @SerializedName( "latestInspection") val latestInspection: DeviceInspectionDto?
)


data class DeviceCreateDto(
    @SerializedName( "brand") val brand: String,
    @SerializedName( "model") val model: String,
    @SerializedName( "variant") val variant: String?,
    @SerializedName( "color") val color: String,
    @SerializedName( "storage") val storage: String,
    @SerializedName( "ram") val ram: String,
    @SerializedName( "imei1") val imei1: String,
    @SerializedName( "imei2") val imei2: String?,
    @SerializedName( "serialNumber") val serialNumber: String?
)


data class DeviceUpdateDto(
    @SerializedName( "brand") val brand: String,
    @SerializedName( "model") val model: String,
    @SerializedName( "variant") val variant: String?,
    @SerializedName( "color") val color: String,
    @SerializedName( "storage") val storage: String,
    @SerializedName( "ram") val ram: String,
    @SerializedName( "imei1") val imei1: String,
    @SerializedName( "imei2") val imei2: String?,
    @SerializedName( "serialNumber") val serialNumber: String?
)


data class DeviceStatusUpdateDto(
    @SerializedName( "status") val status: String
)


