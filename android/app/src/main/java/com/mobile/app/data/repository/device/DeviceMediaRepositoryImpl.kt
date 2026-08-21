package com.mobile.app.data.repository.device

import com.mobile.app.data.mapper.device.DeviceMiscMapper
import com.mobile.app.data.remote.api.DeviceMediaApi
import com.mobile.app.data.remote.api.MediaApi
import com.mobile.app.data.remote.dto.device.DeviceMediaLinkDto
import com.mobile.app.domain.model.device.DeviceMedia
import com.mobile.app.domain.model.device.DeviceMediaType
import com.mobile.app.domain.repository.device.DeviceMediaRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DeviceMediaRepositoryImpl(
    private val deviceMediaApi: DeviceMediaApi,
    private val mediaApi: MediaApi
) : DeviceMediaRepository {

    override suspend fun uploadMedia(file: File): Result<String> {
        return try {
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val uploadResponse = mediaApi.uploadMedia(body, "DEVICE_PHOTO", "devices")
            if (!uploadResponse.isSuccessful || uploadResponse.body() == null) {
                return Result.failure(Exception("Failed to upload media"))
            }
            val mediaId = uploadResponse.body()!!.data!!.id

            // Safe to delete local temporary file after successful upload to server
            if (file.exists()) {
                file.delete()
            }

            Result.success(mediaId.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun linkMedia(
        deviceId: String,
        mediaId: String,
        type: DeviceMediaType
    ): Result<DeviceMedia> {
        return try {
            val linkResponse = deviceMediaApi.linkMediaToDevice(
                id = deviceId,
                mediaId = mediaId,
                linkDto = DeviceMediaLinkDto(mediaId, type.name)
            )
            Result.success(DeviceMiscMapper.mapToDomain(linkResponse))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceMedia(deviceId: String): Result<List<DeviceMedia>> {
        return try {
            val response = deviceMediaApi.getDeviceMedia(deviceId)
            Result.success(response.map { DeviceMiscMapper.mapToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
