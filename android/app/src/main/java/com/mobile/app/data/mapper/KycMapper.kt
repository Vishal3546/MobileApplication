package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.KycDocumentDto
import com.mobile.app.domain.enums.IdType
import com.mobile.app.domain.enums.VerificationStatus
import com.mobile.app.domain.model.KycDocument
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun KycDocumentDto.toDomain(): KycDocument {
    return KycDocument(
        id = id,
        customerId = customerId,
        idType = runCatching { IdType.valueOf(idType) }.getOrDefault(IdType.NATIONAL_ID),
        idNumberMasked = idNumberMasked,
        frontMediaId = frontMediaId,
        backMediaId = backMediaId,
        photoMediaId = photoMediaId,
        verificationStatus = runCatching { VerificationStatus.valueOf(verificationStatus) }.getOrDefault(VerificationStatus.NOT_STARTED),
        verificationNotes = verificationNotes,
        verifiedAt = verifiedAt?.let { 
            runCatching { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }.getOrNull() 
        }
    )
}
