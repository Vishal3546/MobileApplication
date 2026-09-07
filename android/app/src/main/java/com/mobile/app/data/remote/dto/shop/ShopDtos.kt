package com.mobile.app.data.remote.dto.shop

import java.util.UUID

data class ShopDto(
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

data class CreateShopRequestDto(
    val name: String,
    val legalName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val ownerUserId: UUID? = null
)
