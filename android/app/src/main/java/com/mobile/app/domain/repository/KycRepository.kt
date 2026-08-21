package com.mobile.app.domain.repository

import com.mobile.app.data.remote.dto.UploadKycRequestDto
import com.mobile.app.domain.model.KycDocument
import java.util.UUID

interface KycRepository {
    suspend fun getCustomerDocuments(customerId: UUID): Result<List<KycDocument>>
    suspend fun uploadKyc(customerId: UUID, request: UploadKycRequestDto): Result<KycDocument>
    suspend fun approveDocument(customerId: UUID, documentId: UUID, notes: String?): Result<KycDocument>
    suspend fun rejectDocument(customerId: UUID, documentId: UUID, notes: String?): Result<KycDocument>
}
