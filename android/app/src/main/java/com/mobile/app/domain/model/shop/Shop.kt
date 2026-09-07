package com.mobile.app.domain.model.shop

import java.util.UUID

data class Shop(
    val id: UUID,
    val name: String,
    val legalName: String?,
    val phone: String?,
    val email: String?,
    val address: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val ownerUserId: UUID?,
    val active: Boolean,
    val createdAt: String
)
