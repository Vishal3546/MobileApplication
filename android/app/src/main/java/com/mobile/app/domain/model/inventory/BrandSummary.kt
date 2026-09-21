package com.mobile.app.domain.model.inventory

import java.math.BigDecimal

data class BrandSummary(
    val brand: String,
    val count: Int,
    val totalValue: BigDecimal
)
