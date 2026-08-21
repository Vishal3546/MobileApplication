package com.mobile.app.data.remote.dto

import java.util.UUID

data class MediaDto(
    val id: UUID,
    val fileType: String,
    val mimeType: String,
    val fileSize: Long,
    val originalFileName: String
)
