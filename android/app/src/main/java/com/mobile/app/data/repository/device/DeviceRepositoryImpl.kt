package com.mobile.app.data.repository.device

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mobile.app.data.mapper.device.DeviceMapper
import com.mobile.app.data.remote.api.DeviceApi
import com.mobile.app.data.remote.dto.device.DeviceCreateDto
import com.mobile.app.data.remote.dto.device.DeviceStatusUpdateDto
import com.mobile.app.data.remote.dto.device.DeviceUpdateDto
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceCreate
import com.mobile.app.domain.model.device.DeviceStatus
import com.mobile.app.domain.model.device.DeviceUpdate
import com.mobile.app.domain.model.device.ImeiVerificationResult
import com.mobile.app.data.mapper.device.DeviceMiscMapper
import com.mobile.app.domain.repository.device.DeviceRepository
import kotlinx.coroutines.flow.Flow

class DeviceRepositoryImpl(
    private val api: DeviceApi
) : DeviceRepository {

    override suspend fun createDevice(deviceCreate: DeviceCreate): Result<Device> {
        return try {
            val dto = DeviceCreateDto(
                brand = deviceCreate.brand,
                model = deviceCreate.model,
                variant = deviceCreate.variant,
                color = deviceCreate.color,
                storage = deviceCreate.storage,
                ram = deviceCreate.ram,
                imei1 = deviceCreate.imei1,
                imei2 = deviceCreate.imei2,
                serialNumber = deviceCreate.serialNumber
            )
            val response = api.createDevice(dto)
            Result.success(DeviceMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateDevice(id: String, deviceUpdate: DeviceUpdate): Result<Device> {
        return try {
            val dto = DeviceUpdateDto(
                brand = deviceUpdate.brand,
                model = deviceUpdate.model,
                variant = deviceUpdate.variant,
                color = deviceUpdate.color,
                storage = deviceUpdate.storage,
                ram = deviceUpdate.ram,
                imei1 = deviceUpdate.imei1,
                imei2 = deviceUpdate.imei2,
                serialNumber = deviceUpdate.serialNumber
            )
            val response = api.updateDevice(id, dto)
            Result.success(DeviceMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDevice(id: String): Result<Device> {
        return try {
            val response = api.getDevice(id)
            Result.success(DeviceMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getDevices(
        search: String?,
        brand: String?,
        model: String?,
        status: String?
    ): Flow<PagingData<Device>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { DevicePagingSource(api, search, brand, model, status) }
        ).flow
    }

    override suspend fun updateDeviceStatus(id: String, status: DeviceStatus): Result<Device> {
        return try {
            val response = api.updateDeviceStatus(id, DeviceStatusUpdateDto(status.name))
            Result.success(DeviceMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyImei(id: String): Result<ImeiVerificationResult> {
        return try {
            val response = api.verifyImei(id)
            Result.success(DeviceMiscMapper.mapToDomain(response))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceLifecycle(id: String): Result<List<com.mobile.app.domain.model.device.DeviceLifecycleEvent>> {
        return try {
            val response = api.getDeviceLifecycle(id)
            Result.success(response.map { 
                com.mobile.app.domain.model.device.DeviceLifecycleEvent(
                    id = it.id,
                    deviceId = it.deviceId,
                    eventType = com.mobile.app.domain.model.device.LifecycleEventType.valueOf(it.eventType),
                    performerId = it.performerId,
                    branchId = it.branchId,
                    metadata = it.metadata,
                    timestamp = java.time.LocalDateTime.parse(it.timestamp, java.time.format.DateTimeFormatter.ISO_DATE_TIME)
                )
            })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DevicePagingSource(
    private val api: DeviceApi,
    private val search: String?,
    private val brand: String?,
    private val model: String?,
    private val status: String?
) : PagingSource<Int, Device>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Device> {
        val page = params.key ?: 0
        return try {
            val response = api.getDevices(
                page = page,
                size = params.loadSize,
                search = search,
                brand = brand,
                model = model,
                status = status
            )
            LoadResult.Page(
                data = response.items.map { DeviceMapper.mapToDomain(it) },
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (page + 1 >= response.pages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Device>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
