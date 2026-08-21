package com.mobile.app.domain.model

import com.mobile.app.domain.enums.IdType
import com.mobile.app.domain.enums.VerificationStatus
import java.time.LocalDateTime
import java.util.UUID

data class KycDocument(
    val id: UUID,
    val customerId: UUID,
    val idType: IdType,
    val idNumberMasked: String,
    val frontMediaId: UUID?,
    val backMediaId: UUID?,
    val photoMediaId: UUID?,
    val verificationStatus: VerificationStatus,
    val verificationNotes: String?,
    val verifiedAt: LocalDateTime?
)
