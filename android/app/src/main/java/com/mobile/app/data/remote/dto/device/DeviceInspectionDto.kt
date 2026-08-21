package com.mobile.app.data.remote.dto.device

import com.google.gson.annotations.SerializedName



data class DeviceInspectionDto(
    @SerializedName( "id") val id: String,
    @SerializedName( "deviceId") val deviceId: String,
    @SerializedName( "display") val display: String,
    @SerializedName( "touch") val touch: String,
    @SerializedName( "camera") val camera: String,
    @SerializedName( "speaker") val speaker: String,
    @SerializedName( "microphone") val microphone: String,
    @SerializedName( "charging") val charging: String,
    @SerializedName( "wifi") val wifi: String,
    @SerializedName( "bluetooth") val bluetooth: String,
    @SerializedName( "sim") val sim: String,
    @SerializedName( "fingerprint") val fingerprint: String,
    @SerializedName( "faceId") val faceId: String,
    @SerializedName( "battery") val battery: String,
    @SerializedName( "flash") val flash: String,
    @SerializedName( "vibration") val vibration: String,
    @SerializedName( "network") val network: String,
    @SerializedName( "finalStatus") val finalStatus: String,
    @SerializedName( "notes") val notes: String?,
    @SerializedName( "inspectedBy") val inspectedBy: String,
    @SerializedName( "inspectedAt") val inspectedAt: String
)


data class DeviceInspectionCreateDto(
    @SerializedName( "display") val display: String,
    @SerializedName( "touch") val touch: String,
    @SerializedName( "camera") val camera: String,
    @SerializedName( "speaker") val speaker: String,
    @SerializedName( "microphone") val microphone: String,
    @SerializedName( "charging") val charging: String,
    @SerializedName( "wifi") val wifi: String,
    @SerializedName( "bluetooth") val bluetooth: String,
    @SerializedName( "sim") val sim: String,
    @SerializedName( "fingerprint") val fingerprint: String,
    @SerializedName( "faceId") val faceId: String,
    @SerializedName( "battery") val battery: String,
    @SerializedName( "flash") val flash: String,
    @SerializedName( "vibration") val vibration: String,
    @SerializedName( "network") val network: String,
    @SerializedName( "notes") val notes: String?
)


