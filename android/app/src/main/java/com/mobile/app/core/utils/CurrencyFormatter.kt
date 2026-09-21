package com.mobile.app.core.utils

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Formats monetary values as Indian Rupees with Indian digit grouping.
 *
 * Examples:
 *  125000     -> ₹1,25,000
 *  12345678   -> ₹1,23,45,678
 *  1250.50    -> ₹1,250.50
 *  999        -> ₹999
 */
object CurrencyFormatter {

    fun formatRupees(amount: BigDecimal?): String {
        if (amount == null) return "₹0"

        val sign = if (amount.signum() < 0) "-₹" else "₹"
        val value = amount.abs().setScale(2, RoundingMode.HALF_UP)
        val plain = value.toPlainString()                 // e.g. "125000.00"
        val dotIndex = plain.indexOf('.')
        val intPart = plain.substring(0, dotIndex)
        val fraction = plain.substring(dotIndex + 1)       // always 2 chars after setScale(2)

        // Drop the decimals only when they are all zeros ("1250.00" -> "1,250", "1250.50" -> "1,250.50")
        val fractionText = if (fraction.trimEnd('0').isEmpty()) "" else ".$fraction"

        val groupedInteger = groupIndianStyle(intPart)

        return sign + groupedInteger + fractionText
    }

    /**
     * Groups digits Indian style: the last 3 digits stay together and the rest
     * are grouped in pairs. 125000 -> 1,25,000 | 12345678 -> 1,23,45,678
     */
    private fun groupIndianStyle(digits: String): String {
        if (digits.length <= 3) return digits

        val head = digits.substring(0, digits.length - 3)
        val tail = digits.substring(digits.length - 3)

        val groups = mutableListOf<String>()
        var index = head.length
        while (index > 0) {
            val start = maxOf(0, index - 2)
            groups.add(0, head.substring(start, index))
            index = start
        }
        return groups.joinToString(",") + "," + tail
    }
}
