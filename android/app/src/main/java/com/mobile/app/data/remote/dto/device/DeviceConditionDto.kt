package com.mobile.app.data.remote.dto.device

import com.google.gson.annotations.SerializedName



data class DeviceConditionDto(
    @SerializedName( "id") val id: String,
    @SerializedName( "deviceId") val deviceId: String,
    @SerializedName( "batteryHealth") val batteryHealth: Int,
    @SerializedName( "displayCondition") val displayCondition: String,
    @SerializedName( "bodyCondition") val bodyCondition: String,
    @SerializedName( "cameraCondition") val cameraCondition: String,
    @SerializedName( "speakerCondition") val speakerCondition: String,
    @SerializedName( "microphoneCondition") val microphoneCondition: String,
    @SerializedName( "chargingCondition") val chargingCondition: String,
    @SerializedName( "biometricStatus") val biometricStatus: String,
    @SerializedName( "networkLock") val networkLock: Boolean,
    @SerializedName( "originalBill") val originalBill: Boolean,
    @SerializedName( "box") val box: Boolean,
    @SerializedName( "charger") val charger: Boolean,
    @SerializedName( "accessories") val accessories: Boolean,
    @SerializedName( "notes") val notes: String?,
    @SerializedName( "recordedBy") val recordedBy: String,
    @SerializedName( "recordedAt") val recordedAt: String
)


data class DeviceConditionCreateDto(
    @SerializedName( "batteryHealth") val batteryHealth: Int,
    @SerializedName( "displayCondition") val displayCondition: String,
    @SerializedName( "bodyCondition") val bodyCondition: String,
    @SerializedName( "cameraCondition") val cameraCondition: String,
    @SerializedName( "speakerCondition") val speakerCondition: String,
    @SerializedName( "microphoneCondition") val microphoneCondition: String,
    @SerializedName( "chargingCondition") val chargingCondition: String,
    @SerializedName( "biometricStatus") val biometricStatus: String,
    @SerializedName( "networkLock") val networkLock: Boolean,
    @SerializedName( "originalBill") val originalBill: Boolean,
    @SerializedName( "box") val box: Boolean,
    @SerializedName( "charger") val charger: Boolean,
    @SerializedName( "accessories") val accessories: Boolean,
    @SerializedName( "notes") val notes: String?
)


