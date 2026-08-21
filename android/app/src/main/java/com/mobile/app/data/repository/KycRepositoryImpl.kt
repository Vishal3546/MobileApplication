package com.mobile.app.data.repository

import com.mobile.app.data.mapper.toDomain
import com.mobile.app.data.remote.api.KycApi
import com.mobile.app.data.remote.dto.UploadKycRequestDto
import com.mobile.app.domain.model.KycDocument
import com.mobile.app.domain.repository.KycRepository
import java.util.UUID
import javax.inject.Inject

class KycRepositoryImpl @Inject constructor(
    private val kycApi: KycApi
) : KycRepository {

    override suspend fun getCustomerDocuments(customerId: UUID): Result<List<KycDocument>> {
        return try {
            val response = kycApi.getCustomerDocuments(customerId)
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

    override suspend fun uploadKyc(customerId: UUID, request: UploadKycRequestDto): Result<KycDocument> {
        return try {
            val response = kycApi.uploadKyc(customerId, request)
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

    override suspend fun approveDocument(customerId: UUID, documentId: UUID, notes: String?): Result<KycDocument> {
        return try {
            val response = kycApi.approveDocument(customerId, documentId, notes)
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

    override suspend fun rejectDocument(customerId: UUID, documentId: UUID, notes: String?): Result<KycDocument> {
        return try {
            val response = kycApi.rejectDocument(customerId, documentId, notes)
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
