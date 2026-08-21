package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.ConsentDto
import com.mobile.app.domain.enums.ConsentType
import com.mobile.app.domain.model.Consent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun ConsentDto.toDomain(): Consent {
    return Consent(
        id = id,
        customerId = customerId,
        consentType = runCatching { ConsentType.valueOf(consentType) }.getOrDefault(ConsentType.OTHER),
        consentTextVersion = consentTextVersion,
        signatureMediaId = signatureMediaId,
        videoMediaId = videoMediaId,
        capturedAt = capturedAt?.let { 
            runCatching { LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME) }.getOrNull() 
        }
    )
}
