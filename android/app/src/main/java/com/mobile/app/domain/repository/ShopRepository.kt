package com.mobile.app.domain.repository

import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.shop.Shop
import com.mobile.app.data.remote.dto.shop.CreateShopRequestDto
import java.util.UUID

interface ShopRepository {
    suspend fun getShops(): NetworkState<List<Shop>>
    suspend fun createShop(request: CreateShopRequestDto): NetworkState<Shop>
    suspend fun getShopById(id: UUID): NetworkState<Shop>
}
