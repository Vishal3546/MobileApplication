package com.mobile.app.data.remote.dto.phonespecs

import com.google.gson.annotations.SerializedName

data class PhoneApiResponse<T>(
    @SerializedName("status") val status: Boolean,
    @SerializedName("data") val data: T?,
)

data class LatestData(
    @SerializedName("title") val title: String?,
    @SerializedName("phones") val phones: List<PhoneSummaryDto>?,
)

data class SearchData(
    @SerializedName("title") val title: String?,
    @SerializedName("phones") val phones: List<PhoneSummaryDto>?,
)

data class BrandPhonesData(
    @SerializedName("title") val title: String?,
    @SerializedName("current_page") val currentPage: Int?,
    @SerializedName("last_page") val lastPage: Int?,
    @SerializedName("phones") val phones: List<PhoneSummaryDto>?,
)

data class PhoneSummaryDto(
    @SerializedName("brand") val brand: String?,
    @SerializedName("phone_name") val phoneName: String?,
    @SerializedName("slug") val slug: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("detail") val detail: String?,
)

data class BrandDto(
    @SerializedName("brand_id") val brandId: Int?,
    @SerializedName("brand_name") val brandName: String?,
    @SerializedName("brand_slug") val brandSlug: String?,
    @SerializedName("device_count") val deviceCount: Int?,
)

data class PhoneDetailData(
    @SerializedName("brand") val brand: String?,
    @SerializedName("phone_name") val phoneName: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("phone_images") val phoneImages: List<String>?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("dimension") val dimension: String?,
    @SerializedName("os") val os: String?,
    @SerializedName("storage") val storage: String?,
    @SerializedName("specifications") val specifications: List<SpecificationGroupDto>?,
)

data class SpecificationGroupDto(
    @SerializedName("title") val title: String?,
    @SerializedName("specs") val specs: List<SpecificationItemDto>?,
)

data class SpecificationItemDto(
    @SerializedName("key") val key: String?,
    @SerializedName("val") val values: List<String>?,
)
