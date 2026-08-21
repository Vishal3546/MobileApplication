package com.mobile.app.data.repository

import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.ConsentApi
import com.mobile.app.data.remote.dto.CaptureConsentRequestDto
import com.mobile.app.domain.model.Consent
import com.mobile.app.domain.repository.ConsentRepository
import java.util.UUID
import javax.inject.Inject

class ConsentRepositoryImpl @Inject constructor(
    private val consentApi: ConsentApi
) : ConsentRepository {

    override suspend fun getCustomerConsents(customerId: UUID): Result<List<Consent>> {
        return try {
            val response = consentApi.getCustomerConsents(customerId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data.map { it.toDomain() })
            } else {
                Result.failure(Exception(body?.message ?: "API Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun captureConsent(customerId: UUID, request: CaptureConsentRequestDto): Result<Consent> {
        return try {
            val response = consentApi.captureConsent(customerId, request)
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
}
