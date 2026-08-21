package com.mobile.app.data.remote.dto

import java.util.UUID

data class ConsentDto(
    val id: UUID,
    val customerId: UUID,
    val consentType: String,
    val consentTextVersion: String,
    val signatureMediaId: UUID?,
    val videoMediaId: UUID?,
    val capturedAt: String?
)

data class CaptureConsentRequestDto(
    val consentType: String,
    val consentTextVersion: String,
    val signatureMediaId: UUID?,
    val videoMediaId: UUID?,
    val ipAddress: String?,
    val deviceInfo: String?
)
