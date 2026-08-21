package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.MediaDto
import com.mobile.app.domain.enums.MediaType
import com.mobile.app.domain.model.MediaFile

fun MediaDto.toDomain(): MediaFile {
    return MediaFile(
        id = id,
        fileType = runCatching { MediaType.valueOf(fileType) }.getOrDefault(MediaType.OTHER),
        mimeType = mimeType,
        fileSize = fileSize,
        originalFileName = originalFileName
    )
}
