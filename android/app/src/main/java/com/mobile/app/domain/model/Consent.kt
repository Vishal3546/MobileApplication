package com.mobile.app.domain.model

import com.mobile.app.domain.enums.ConsentType
import java.time.LocalDateTime
import java.util.UUID

data class Consent(
    val id: UUID,
    val customerId: UUID,
    val consentType: ConsentType,
    val consentTextVersion: String,
    val signatureMediaId: UUID?,
    val videoMediaId: UUID?,
    val capturedAt: LocalDateTime?
)
