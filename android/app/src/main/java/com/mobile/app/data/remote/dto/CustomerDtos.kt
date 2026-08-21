package com.mobile.app.data.remote.dto

import java.util.UUID

data class CustomerDto(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val altPhone: String?,
    val email: String?,
    val address: String?,
    val status: String,
    val verificationStatus: String
)

data class CreateCustomerRequestDto(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val altPhone: String?,
    val email: String?,
    val address: String?
)

data class UpdateCustomerRequestDto(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val altPhone: String?,
    val email: String?,
    val address: String?,
    val status: String?
)
