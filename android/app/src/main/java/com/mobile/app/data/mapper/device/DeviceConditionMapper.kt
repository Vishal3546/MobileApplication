package com.mobile.app.data.mapper.device

import com.mobile.app.data.remote.dto.device.DeviceConditionCreateDto
import com.mobile.app.data.remote.dto.device.DeviceConditionDto
import com.mobile.app.domain.model.device.ConditionStatus
import com.mobile.app.domain.model.device.DeviceCondition
import com.mobile.app.domain.model.device.DeviceConditionCreate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DeviceConditionMapper {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    fun mapToDomain(dto: DeviceConditionDto): DeviceCondition {
        return DeviceCondition(
            id = dto.id,
            deviceId = dto.deviceId,
            batteryHealth = dto.batteryHealth,
            displayCondition = ConditionStatus.valueOf(dto.displayCondition),
            bodyCondition = ConditionStatus.valueOf(dto.bodyCondition),
            cameraCondition = ConditionStatus.valueOf(dto.cameraCondition),
            speakerCondition = ConditionStatus.valueOf(dto.speakerCondition),
            microphoneCondition = ConditionStatus.valueOf(dto.microphoneCondition),
            chargingCondition = ConditionStatus.valueOf(dto.chargingCondition),
            biometricStatus = ConditionStatus.valueOf(dto.biometricStatus),
            networkLock = dto.networkLock,
            originalBill = dto.originalBill,
            box = dto.box,
            charger = dto.charger,
            accessories = dto.accessories,
            notes = dto.notes,
            recordedBy = dto.recordedBy,
            recordedAt = LocalDateTime.parse(dto.recordedAt, formatter)
        )
    }

    fun mapToDto(domain: DeviceConditionCreate): DeviceConditionCreateDto {
        return DeviceConditionCreateDto(
            batteryHealth = domain.batteryHealth,
            displayCondition = domain.displayCondition.name,
            bodyCondition = domain.bodyCondition.name,
            cameraCondition = domain.cameraCondition.name,
            speakerCondition = domain.speakerCondition.name,
            microphoneCondition = domain.microphoneCondition.name,
            chargingCondition = domain.chargingCondition.name,
            biometricStatus = domain.biometricStatus.name,
            networkLock = domain.networkLock,
            originalBill = domain.originalBill,
            box = domain.box,
            charger = domain.charger,
            accessories = domain.accessories,
            notes = domain.notes
        )
    }
}
