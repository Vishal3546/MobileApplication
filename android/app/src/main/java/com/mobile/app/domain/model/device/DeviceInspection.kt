package com.mobile.app.domain.model.device

import java.time.LocalDateTime

data class DeviceInspection(
    val id: String,
    val deviceId: String,
    val display: InspectionStatus,
    val touch: InspectionStatus,
    val camera: InspectionStatus,
    val speaker: InspectionStatus,
    val microphone: InspectionStatus,
    val charging: InspectionStatus,
    val wifi: InspectionStatus,
    val bluetooth: InspectionStatus,
    val sim: InspectionStatus,
    val fingerprint: InspectionStatus,
    val faceId: InspectionStatus,
    val battery: InspectionStatus,
    val flash: InspectionStatus,
    val vibration: InspectionStatus,
    val network: InspectionStatus,
    val finalStatus: InspectionStatus,
    val notes: String?,
    val inspectedBy: String,
    val inspectedAt: LocalDateTime
)

data class DeviceInspectionCreate(
    val display: InspectionStatus,
    val touch: InspectionStatus,
    val camera: InspectionStatus,
    val speaker: InspectionStatus,
    val microphone: InspectionStatus,
    val charging: InspectionStatus,
    val wifi: InspectionStatus,
    val bluetooth: InspectionStatus,
    val sim: InspectionStatus,
    val fingerprint: InspectionStatus,
    val faceId: InspectionStatus,
    val battery: InspectionStatus,
    val flash: InspectionStatus,
    val vibration: InspectionStatus,
    val network: InspectionStatus,
    val notes: String?
)
