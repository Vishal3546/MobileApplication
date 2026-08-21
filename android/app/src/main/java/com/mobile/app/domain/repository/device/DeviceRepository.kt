package com.mobile.app.domain.repository.device

import androidx.paging.PagingData
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceCreate
import com.mobile.app.domain.model.device.DeviceStatus
import com.mobile.app.domain.model.device.DeviceUpdate
import com.mobile.app.domain.model.device.ImeiVerificationResult
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    suspend fun createDevice(deviceCreate: DeviceCreate): Result<Device>
    suspend fun updateDevice(id: String, deviceUpdate: DeviceUpdate): Result<Device>
    suspend fun getDevice(id: String): Result<Device>
    fun getDevices(
        search: String?,
        brand: String?,
        model: String?,
        status: String?
    ): Flow<PagingData<Device>>
    suspend fun updateDeviceStatus(id: String, status: DeviceStatus): Result<Device>
    suspend fun verifyImei(id: String): Result<ImeiVerificationResult>
    suspend fun getDeviceLifecycle(id: String): Result<List<com.mobile.app.domain.model.device.DeviceLifecycleEvent>>
}
