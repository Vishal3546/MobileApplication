package com.mobile.app.domain.repository

import com.mobile.app.data.remote.dto.CaptureConsentRequestDto
import com.mobile.app.domain.model.Consent
import java.util.UUID

interface ConsentRepository {
    suspend fun getCustomerConsents(customerId: UUID): Result<List<Consent>>
    suspend fun captureConsent(customerId: UUID, request: CaptureConsentRequestDto): Result<Consent>
}
