package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.device.DeviceMediaDto
import com.mobile.app.data.remote.dto.device.DeviceMediaLinkDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DeviceMediaApi {
    @POST("api/v1/devices/{id}/media/{mediaId}")
    suspend fun linkMediaToDevice(
        @Path("id") id: String,
        @Path("mediaId") mediaId: String,
        @Body linkDto: DeviceMediaLinkDto
    ): DeviceMediaDto

    @GET("api/v1/devices/{id}/media")
    suspend fun getDeviceMedia(@Path("id") id: String): List<DeviceMediaDto>
}
