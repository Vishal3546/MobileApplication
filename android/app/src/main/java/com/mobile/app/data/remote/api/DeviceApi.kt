package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.device.DeviceCreateDto
import com.mobile.app.data.remote.dto.device.DeviceDto
import com.mobile.app.data.remote.dto.device.DeviceListResponseDto
import com.mobile.app.data.remote.dto.device.DeviceStatusUpdateDto
import com.mobile.app.data.remote.dto.device.DeviceUpdateDto
import com.mobile.app.data.remote.dto.device.ImeiVerificationResultDto
import com.mobile.app.data.remote.dto.device.DeviceLifecycleEventDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface DeviceApi {
    @POST("api/v1/devices")
    suspend fun createDevice(@Body deviceCreateDto: DeviceCreateDto): DeviceDto

    @PUT("api/v1/devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body deviceUpdateDto: DeviceUpdateDto
    ): DeviceDto

    @GET("api/v1/devices/{id}")
    suspend fun getDevice(@Path("id") id: String): DeviceDto

    @GET("api/v1/devices")
    suspend fun getDevices(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("search") search: String?,
        @Query("brand") brand: String?,
        @Query("model") model: String?,
        @Query("status") status: String?
    ): DeviceListResponseDto

    @PATCH("api/v1/devices/{id}/status")
    suspend fun updateDeviceStatus(
        @Path("id") id: String,
        @Body statusUpdateDto: DeviceStatusUpdateDto
    ): DeviceDto

    @POST("api/v1/devices/{id}/verify-imei")
    suspend fun verifyImei(@Path("id") id: String): ImeiVerificationResultDto

    @GET("api/v1/devices/{id}/lifecycle")
    suspend fun getDeviceLifecycle(@Path("id") id: String): List<DeviceLifecycleEventDto>
}
