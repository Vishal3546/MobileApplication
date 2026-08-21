package com.mobile.app.domain.repository.device

import com.mobile.app.domain.model.device.DeviceInspection
import com.mobile.app.domain.model.device.DeviceInspectionCreate

interface DeviceInspectionRepository {
    suspend fun createInspection(deviceId: String, inspection: DeviceInspectionCreate): Result<DeviceInspection>
    suspend fun getInspectionHistory(deviceId: String): Result<List<DeviceInspection>>
}
