package com.mobile.app.data.remote.dto.device

import com.google.gson.annotations.SerializedName



data class ImeiVerificationResultDto(
    @SerializedName( "deviceId") val deviceId: String,
    @SerializedName( "state") val state: String,
    @SerializedName( "message") val message: String?,
    @SerializedName( "verifiedAt") val verifiedAt: String
)


data class DeviceLifecycleEventDto(
    @SerializedName( "id") val id: String,
    @SerializedName( "deviceId") val deviceId: String,
    @SerializedName( "eventType") val eventType: String,
    @SerializedName( "performerId") val performerId: String,
    @SerializedName( "branchId") val branchId: String?,
    @SerializedName( "metadata") val metadata: String?,
    @SerializedName( "timestamp") val timestamp: String
)


data class DeviceMediaLinkDto(
    @SerializedName( "mediaId") val mediaId: String,
    @SerializedName( "type") val type: String
)


data class DeviceMediaDto(
    @SerializedName( "id") val id: String,
    @SerializedName( "deviceId") val deviceId: String,
    @SerializedName( "mediaId") val mediaId: String,
    @SerializedName( "type") val type: String,
    @SerializedName( "url") val url: String,
    @SerializedName( "uploadedBy") val uploadedBy: String,
    @SerializedName( "uploadedAt") val uploadedAt: String
)


data class DeviceListResponseDto(
    @SerializedName("content") val content: List<DeviceDto>? = null,
    @SerializedName("items") val items: List<DeviceDto>? = null,
    @SerializedName("totalElements") val totalElements: Int = 0,
    @SerializedName("totalPages") val totalPages: Int = 0,
    @SerializedName("number") val pageNumber: Int = 0,
    @SerializedName("size") val pageSize: Int = 0,
    @SerializedName("last") val isLast: Boolean = true
) {
    val deviceList: List<DeviceDto>
        get() = content ?: items ?: emptyList()
}


