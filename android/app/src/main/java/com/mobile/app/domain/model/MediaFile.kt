package com.mobile.app.domain.model

import com.mobile.app.domain.enums.MediaType
import java.util.UUID

data class MediaFile(
    val id: UUID,
    val fileType: MediaType,
    val mimeType: String,
    val fileSize: Long,
    val originalFileName: String
)
