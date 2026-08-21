package com.mobile.app.domain.model.device

import java.time.LocalDateTime

data class ImeiVerificationResult(
    val deviceId: String,
    val state: ImeiVerificationState,
    val message: String?,
    val verifiedAt: LocalDateTime
)

data class DeviceLifecycleEvent(
    val id: String,
    val deviceId: String,
    val eventType: LifecycleEventType,
    val performerId: String,
    val branchId: String?,
    val metadata: String?,
    val timestamp: LocalDateTime
)

data class DeviceMediaLink(
    val mediaId: String,
    val type: DeviceMediaType
)

data class DeviceMedia(
    val id: String,
    val deviceId: String,
    val mediaId: String,
    val type: DeviceMediaType,
    val url: String,
    val uploadedBy: String,
    val uploadedAt: LocalDateTime
)
