package com.mobile.app.data.repository

import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.MediaApi
import com.mobile.app.domain.model.MediaFile
import com.mobile.app.domain.repository.MediaRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.util.UUID
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val mediaApi: MediaApi
) : MediaRepository {

    override suspend fun uploadMedia(file: File, type: String, bucket: String): Result<MediaFile> {
        return try {
            val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestFile)
            
            val response = mediaApi.uploadMedia(multipartBody, type, bucket)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data.toDomain())
            } else {
                Result.failure(Exception(body?.message ?: "API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun downloadMedia(id: UUID): Result<ResponseBody> {
        return try {
            val response = mediaApi.downloadMedia(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to download media"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
