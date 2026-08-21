package com.mobile.app.domain.model

import com.mobile.app.domain.enums.CustomerStatus
import com.mobile.app.domain.enums.VerificationStatus
import java.util.UUID

data class Customer(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val altPhone: String?,
    val email: String?,
    val address: String?,
    val status: CustomerStatus,
    val verificationStatus: VerificationStatus
)
