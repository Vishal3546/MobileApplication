package com.mobile.app.domain.repository

import com.mobile.app.domain.model.device.BrandInfo
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.ModelInfo

data class PhoneSpecDetail(
    val brand: String,
    val model: String,
    val thumbnail: String?,
    val images: List<String>,
    val releaseDate: String?,
    val dimension: String?,
    val os: String?,
    val storage: String?,
    val ram: String?,
)

interface PhoneSpecsRepository {
    suspend fun getLatestPhones(): Result<List<ModelInfo>>
    suspend fun getBrands(): Result<List<BrandInfo>>
    suspend fun searchPhone(query: String): Result<List<ModelInfo>>
    suspend fun getModelsForBrand(brand: String): Result<List<ModelInfo>>
    suspend fun getPhoneDetails(slug: String): Result<PhoneSpecDetail>
    suspend fun getDeviceInfoByImei(imei: String): Result<Device>
}
