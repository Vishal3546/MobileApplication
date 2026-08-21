package com.mobile.app.domain.model.device

import java.time.LocalDateTime

data class DeviceCondition(
    val id: String,
    val deviceId: String,
    val batteryHealth: Int, // 0-100
    val displayCondition: ConditionStatus,
    val bodyCondition: ConditionStatus,
    val cameraCondition: ConditionStatus,
    val speakerCondition: ConditionStatus,
    val microphoneCondition: ConditionStatus,
    val chargingCondition: ConditionStatus,
    val biometricStatus: ConditionStatus,
    val networkLock: Boolean,
    val originalBill: Boolean,
    val box: Boolean,
    val charger: Boolean,
    val accessories: Boolean,
    val notes: String?,
    val recordedBy: String,
    val recordedAt: LocalDateTime
)

data class DeviceConditionCreate(
    val batteryHealth: Int,
    val displayCondition: ConditionStatus,
    val bodyCondition: ConditionStatus,
    val cameraCondition: ConditionStatus,
    val speakerCondition: ConditionStatus,
    val microphoneCondition: ConditionStatus,
    val chargingCondition: ConditionStatus,
    val biometricStatus: ConditionStatus,
    val networkLock: Boolean,
    val originalBill: Boolean,
    val box: Boolean,
    val charger: Boolean,
    val accessories: Boolean,
    val notes: String?
)
