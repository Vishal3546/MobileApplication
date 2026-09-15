package com.mobile.app.data.repository

import com.mobile.app.core.utils.TacLookupHelper
import com.mobile.app.data.remote.api.PhoneSpecsApi
import com.mobile.app.domain.model.device.BrandInfo
import com.mobile.app.domain.model.device.Device
import com.mobile.app.domain.model.device.DeviceStatus
import com.mobile.app.domain.model.device.ImeiVerificationState
import com.mobile.app.domain.model.device.ModelInfo
import com.mobile.app.domain.repository.PhoneSpecDetail
import com.mobile.app.domain.repository.PhoneSpecsRepository
import java.time.LocalDateTime
import javax.inject.Inject

class PhoneSpecsRepositoryImpl @Inject constructor(
    private val phoneSpecsApi: PhoneSpecsApi,
) : PhoneSpecsRepository {

    override suspend fun getLatestPhones(): Result<List<ModelInfo>> {
        return try {
            val response = phoneSpecsApi.getLatestPhones()
            val phones = response.data?.phones?.map { dto ->
                ModelInfo(
                    name = dto.phoneName ?: dto.brand ?: "Unknown Device",
                    imageUrl = dto.image ?: "",
                )
            } ?: emptyList()
            Result.success(phones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBrands(): Result<List<BrandInfo>> {
        return try {
            val response = phoneSpecsApi.getBrands()
            val brands = response.data?.map { dto ->
                BrandInfo(
                    name = dto.brandName ?: "Unknown",
                    imageUrl = "https://logo.clearbit.com/${(dto.brandName ?: "").lowercase().replace(" ", "")}.com",
                )
            } ?: emptyList()
            Result.success(brands)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchPhone(query: String): Result<List<ModelInfo>> {
        return try {
            val response = phoneSpecsApi.searchPhone(query)
            val phones = response.data?.phones?.map { dto ->
                ModelInfo(
                    name = dto.phoneName ?: "Unknown",
                    imageUrl = dto.image ?: "",
                )
            } ?: emptyList()
            Result.success(phones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPhoneDetails(slug: String): Result<PhoneSpecDetail> {
        return try {
            val response = phoneSpecsApi.getPhoneDetails(slug)
            val data = response.data ?: throw IllegalStateException("Device details not found")
            
            var ram = "8 GB"
            var storage = "128 GB"
            
            data.specifications?.forEach { specGroup ->
                specGroup.specs?.forEach { item ->
                    val key = item.key?.lowercase() ?: ""
                    val value = item.values?.joinToString(" ") ?: ""
                    if (key.contains("internal") || key.contains("memory")) {
                        if (value.contains("RAM", ignoreCase = true)) {
                            ram = parseRam(value)
                        }
                        if (value.contains("GB") || value.contains("TB")) {
                            storage = parseStorage(value)
                        }
                    }
                }
            }

            val detail = PhoneSpecDetail(
                brand = data.brand ?: "Generic",
                model = data.phoneName ?: "Smartphone",
                thumbnail = data.thumbnail,
                images = data.phoneImages ?: emptyList(),
                releaseDate = data.releaseDate,
                dimension = data.dimension,
                os = data.os,
                storage = storage,
                ram = ram,
            )
            Result.success(detail)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDeviceInfoByImei(imei: String): Result<Device> {
        val tacMatch = TacLookupHelper.lookup(imei)
        val searchBrand = tacMatch?.first ?: "Generic"
        val searchModel = tacMatch?.second ?: "Smartphone"

        return try {
            val searchResult = phoneSpecsApi.searchPhone("$searchBrand $searchModel")
            val firstPhone = searchResult.data?.phones?.firstOrNull()

            if (firstPhone?.slug != null) {
                val detailsResult = getPhoneDetails(firstPhone.slug)
                val details = detailsResult.getOrNull()

                val device = createFallbackDevice(
                    brand = details?.brand ?: searchBrand,
                    model = details?.model ?: searchModel,
                    storage = details?.storage ?: "128 GB",
                    ram = details?.ram ?: "8 GB",
                    imei = imei,
                )
                Result.success(device)
            } else {
                val device = createFallbackDevice(
                    brand = searchBrand,
                    model = searchModel,
                    storage = "128 GB",
                    ram = "8 GB",
                    imei = imei,
                )
                Result.success(device)
            }
        } catch (_: Exception) {
            val device = createFallbackDevice(
                brand = searchBrand,
                model = searchModel,
                storage = "128 GB",
                ram = "8 GB",
                imei = imei,
            )
            Result.success(device)
        }
    }

    private fun createFallbackDevice(
        brand: String,
        model: String,
        storage: String,
        ram: String,
        imei: String,
    ): Device {
        val now = LocalDateTime.now()
        return Device(
            id = "temp_${System.currentTimeMillis()}",
            brand = brand,
            model = model,
            variant = "Standard",
            color = "Black",
            storage = storage,
            ram = ram,
            imei1 = imei,
            imei2 = null,
            serialNumber = null,
            status = DeviceStatus.ACTIVE,
            branchId = "main",
            createdBy = "system",
            updatedBy = null,
            createdAt = now,
            updatedAt = now,
            verificationState = ImeiVerificationState.VERIFIED,
            mediaCount = 0,
            latestCondition = null,
            latestInspection = null,
        )
    }

    private fun parseRam(specText: String): String {
        val regex = Regex("(\\d+)\\s*(GB|MB)\\s*RAM", RegexOption.IGNORE_CASE)
        val match = regex.find(specText)
        return if (match != null) "${match.groupValues[1]} GB" else "8 GB"
    }

    private fun parseStorage(specText: String): String {
        val regex = Regex("(\\d+)\\s*(GB|TB)", RegexOption.IGNORE_CASE)
        val match = regex.find(specText)
        return if (match != null) "${match.groupValues[1]} ${match.groupValues[2]}" else "128 GB"
    }
}
