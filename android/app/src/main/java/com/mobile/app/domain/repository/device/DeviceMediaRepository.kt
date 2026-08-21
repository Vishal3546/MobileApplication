package com.mobile.app.domain.repository.device

import com.mobile.app.domain.model.device.DeviceMedia
import com.mobile.app.domain.model.device.DeviceMediaType
import java.io.File

interface DeviceMediaRepository {
    suspend fun uploadMedia(file: File): Result<String>
    suspend fun linkMedia(deviceId: String, mediaId: String, type: DeviceMediaType): Result<DeviceMedia>
    suspend fun getDeviceMedia(deviceId: String): Result<List<DeviceMedia>>
}
