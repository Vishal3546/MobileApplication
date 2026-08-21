package com.mobile.app.domain.repository

import com.mobile.app.domain.model.MediaFile
import okhttp3.ResponseBody
import java.io.File
import java.util.UUID

interface MediaRepository {
    suspend fun uploadMedia(file: File, type: String, bucket: String = "default"): Result<MediaFile>
    suspend fun downloadMedia(id: UUID): Result<ResponseBody>
}
