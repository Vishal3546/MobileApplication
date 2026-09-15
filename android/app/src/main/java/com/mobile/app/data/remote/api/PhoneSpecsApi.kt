package com.mobile.app.data.remote.api

import com.mobile.app.data.remote.dto.phonespecs.BrandDto
import com.mobile.app.data.remote.dto.phonespecs.LatestData
import com.mobile.app.data.remote.dto.phonespecs.PhoneApiResponse
import com.mobile.app.data.remote.dto.phonespecs.PhoneDetailData
import com.mobile.app.data.remote.dto.phonespecs.SearchData
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhoneSpecsApi {
    @GET("latest")
    suspend fun getLatestPhones(): PhoneApiResponse<LatestData>

    @GET("search")
    suspend fun searchPhone(@Query("query") query: String): PhoneApiResponse<SearchData>

    @GET("phones/{slug}")
    suspend fun getPhoneDetails(@Path("slug") slug: String): PhoneApiResponse<PhoneDetailData>

    @GET("brands")
    suspend fun getBrands(): PhoneApiResponse<List<BrandDto>>
}
