package com.mobile.app.data.remote.dto

import java.util.UUID

data class KycDocumentDto(
    val id: UUID,
    val customerId: UUID,
    val idType: String,
    val idNumberMasked: String,
    val frontMediaId: UUID?,
    val backMediaId: UUID?,
    val photoMediaId: UUID?,
    val verificationStatus: String,
    val verificationNotes: String?,
    val verifiedAt: String?
)

data class UploadKycRequestDto(
    val idType: String,
    val idNumber: String,
    val frontMediaId: UUID?,
    val backMediaId: UUID?,
    val photoMediaId: UUID?
)
