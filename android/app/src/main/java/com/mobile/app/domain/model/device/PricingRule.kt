package com.mobile.app.domain.model.device

import java.math.BigDecimal

data class PricingBreakdown(
    val basePrice: BigDecimal,
    val deductions: List<Deduction>,
    val finalPrice: BigDecimal
)

data class Deduction(
    val reason: String,
    val amount: BigDecimal
)

object PricingCalculator {
    
    fun calculatePrice(
        brand: String,
        model: String,
        inspection: DeviceInspectionCreate?,
        condition: DeviceConditionCreate?
    ): PricingBreakdown {
        // Base Price Mock Logic (In real app, fetch from DB/API)
        var currentPrice = when {
            model.contains("Pro Max") -> BigDecimal("45000")
            model.contains("Ultra") -> BigDecimal("50000")
            brand == "Apple" -> BigDecimal("35000")
            brand == "Samsung" -> BigDecimal("25000")
            else -> BigDecimal("15000")
        }
        
        val basePrice = currentPrice
        val deductions = mutableListOf<Deduction>()

        // 1. Functional Inspection Deductions
        inspection?.let {
            if (it.display == InspectionStatus.FAIL) {
                val d = basePrice.multiply(BigDecimal("0.25")) // 25% off
                deductions.add(Deduction("Display Issue", d))
                currentPrice = currentPrice.subtract(d)
            }
            if (it.camera == InspectionStatus.FAIL) {
                val d = basePrice.multiply(BigDecimal("0.15")) // 15% off
                deductions.add(Deduction("Camera Issue", d))
                currentPrice = currentPrice.subtract(d)
            }
            if (it.battery == InspectionStatus.FAIL) {
                val d = basePrice.multiply(BigDecimal("0.10"))
                deductions.add(Deduction("Battery Functional Issue", d))
                currentPrice = currentPrice.subtract(d)
            }
        }

        // 2. Physical Condition Deductions
        condition?.let {
            // Battery Health Deduction
            if (it.batteryHealth < 85) {
                val d = basePrice.multiply(BigDecimal("0.05"))
                deductions.add(Deduction("Low Battery Health (<85%)", d))
                currentPrice = currentPrice.subtract(d)
            }

            // Body Condition
            when (it.bodyCondition) {
                ConditionStatus.FAIR -> {
                    val d = basePrice.multiply(BigDecimal("0.05"))
                    deductions.add(Deduction("Fair Body Condition", d))
                    currentPrice = currentPrice.subtract(d)
                }
                ConditionStatus.POOR, ConditionStatus.DAMAGED -> {
                    val d = basePrice.multiply(BigDecimal("0.15"))
                    deductions.add(Deduction("Heavy Body Damage", d))
                    currentPrice = currentPrice.subtract(d)
                }
                else -> {}
            }

            // Inclusions (Box/Bill)
            if (!it.originalBill) {
                val d = BigDecimal("500")
                deductions.add(Deduction("Missing Original Bill", d))
                currentPrice = currentPrice.subtract(d)
            }
            if (!it.box) {
                val d = BigDecimal("300")
                deductions.add(Deduction("Missing Original Box", d))
                currentPrice = currentPrice.subtract(d)
            }
        }

        // Ensure price doesn't go below a minimum (e.g., scrap value)
        val minPrice = basePrice.multiply(BigDecimal("0.10"))
        if (currentPrice < minPrice) {
            currentPrice = minPrice
        }

        return PricingBreakdown(
            basePrice = basePrice,
            deductions = deductions,
            finalPrice = currentPrice
        )
    }
}
