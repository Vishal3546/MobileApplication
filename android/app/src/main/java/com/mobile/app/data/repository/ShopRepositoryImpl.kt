package com.mobile.app.data.repository

import com.mobile.app.data.remote.api.ShopApi
import com.mobile.app.data.remote.dto.shop.CreateShopRequestDto
import com.mobile.app.data.remote.dto.shop.ShopDto
import com.mobile.app.domain.model.NetworkState
import com.mobile.app.domain.model.safeApiCall
import com.mobile.app.domain.model.map
import com.mobile.app.domain.model.shop.Shop
import com.mobile.app.domain.repository.ShopRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopApi: ShopApi
) : ShopRepository {

    override suspend fun getShops(): NetworkState<List<Shop>> {
        return safeApiCall { shopApi.getShops() }.map { response ->
            response.data.map { it.toDomain() }
        }
    }

    override suspend fun createShop(request: CreateShopRequestDto): NetworkState<Shop> {
        return safeApiCall { shopApi.createShop(request) }.map { response ->
            response.data.toDomain()
        }
    }

    override suspend fun getShopById(id: UUID): NetworkState<Shop> {
        return safeApiCall { shopApi.getShopById(id) }.map { response ->
            response.data.toDomain()
        }
    }

    private fun ShopDto.toDomain(): Shop {
        return Shop(
            id = id,
            name = name,
            legalName = legalName,
            phone = phone,
            email = email,
            address = address,
            city = city,
            state = state,
            postalCode = postalCode,
            ownerUserId = ownerUserId,
            active = active,
            createdAt = createdAt
        )
    }
}
