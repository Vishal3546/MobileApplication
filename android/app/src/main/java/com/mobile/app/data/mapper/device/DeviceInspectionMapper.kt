package com.mobile.app.data.mapper.device

import com.mobile.app.data.remote.dto.device.DeviceInspectionCreateDto
import com.mobile.app.data.remote.dto.device.DeviceInspectionDto
import com.mobile.app.domain.model.device.DeviceInspection
import com.mobile.app.domain.model.device.DeviceInspectionCreate
import com.mobile.app.domain.model.device.InspectionStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DeviceInspectionMapper {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    fun mapToDomain(dto: DeviceInspectionDto): DeviceInspection {
        return DeviceInspection(
            id = dto.id,
            deviceId = dto.deviceId,
            display = InspectionStatus.valueOf(dto.display),
            touch = InspectionStatus.valueOf(dto.touch),
            camera = InspectionStatus.valueOf(dto.camera),
            speaker = InspectionStatus.valueOf(dto.speaker),
            microphone = InspectionStatus.valueOf(dto.microphone),
            charging = InspectionStatus.valueOf(dto.charging),
            wifi = InspectionStatus.valueOf(dto.wifi),
            bluetooth = InspectionStatus.valueOf(dto.bluetooth),
            sim = InspectionStatus.valueOf(dto.sim),
            fingerprint = InspectionStatus.valueOf(dto.fingerprint),
            faceId = InspectionStatus.valueOf(dto.faceId),
            battery = InspectionStatus.valueOf(dto.battery),
            flash = InspectionStatus.valueOf(dto.flash),
            vibration = InspectionStatus.valueOf(dto.vibration),
            network = InspectionStatus.valueOf(dto.network),
            finalStatus = InspectionStatus.valueOf(dto.finalStatus),
            notes = dto.notes,
            inspectedBy = dto.inspectedBy,
            inspectedAt = LocalDateTime.parse(dto.inspectedAt, formatter)
        )
    }

    fun mapToDto(domain: DeviceInspectionCreate): DeviceInspectionCreateDto {
        return DeviceInspectionCreateDto(
            display = domain.display.name,
            touch = domain.touch.name,
            camera = domain.camera.name,
            speaker = domain.speaker.name,
            microphone = domain.microphone.name,
            charging = domain.charging.name,
            wifi = domain.wifi.name,
            bluetooth = domain.bluetooth.name,
            sim = domain.sim.name,
            fingerprint = domain.fingerprint.name,
            faceId = domain.faceId.name,
            battery = domain.battery.name,
            flash = domain.flash.name,
            vibration = domain.vibration.name,
            network = domain.network.name,
            notes = domain.notes
        )
    }
}
