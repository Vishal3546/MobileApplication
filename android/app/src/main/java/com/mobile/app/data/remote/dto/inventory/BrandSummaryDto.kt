package com.mobile.app.data.remote.dto.inventory

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BrandSummaryDto(
    @SerializedName("brand") val brand: String?,
    @SerializedName("count") val count: Long?,
    @SerializedName("totalValue") val totalValue: BigDecimal?
)
