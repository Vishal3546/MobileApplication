package com.mobile.app.core.utils

object TacLookupHelper {

    private val tacMap = mapOf(
        // Apple iPhones
        "35293611" to Pair("Apple", "iPhone 15 Pro Max"),
        "35293411" to Pair("Apple", "iPhone 15 Pro"),
        "35293211" to Pair("Apple", "iPhone 15 Plus"),
        "35293011" to Pair("Apple", "iPhone 15"),
        "35368311" to Pair("Apple", "iPhone 14 Pro Max"),
        "35368111" to Pair("Apple", "iPhone 14 Pro"),
        "35367911" to Pair("Apple", "iPhone 14 Plus"),
        "35367711" to Pair("Apple", "iPhone 14"),
        "35304911" to Pair("Apple", "iPhone 13 Pro Max"),
        "35304711" to Pair("Apple", "iPhone 13 Pro"),
        "35304511" to Pair("Apple", "iPhone 13"),
        "35304311" to Pair("Apple", "iPhone 13 mini"),
        "35445211" to Pair("Apple", "iPhone 12 Pro Max"),
        "35444911" to Pair("Apple", "iPhone 12 Pro"),
        "35444711" to Pair("Apple", "iPhone 12"),
        "35444511" to Pair("Apple", "iPhone 12 mini"),
        "35291211" to Pair("Apple", "iPhone 11 Pro Max"),
        "35291011" to Pair("Apple", "iPhone 11 Pro"),
        "35290811" to Pair("Apple", "iPhone 11"),

        // Samsung Galaxy
        "35158285" to Pair("Samsung", "Galaxy S24 Ultra"),
        "35158085" to Pair("Samsung", "Galaxy S24+"),
        "35157885" to Pair("Samsung", "Galaxy S24"),
        "35682856" to Pair("Samsung", "Galaxy S23 Ultra"),
        "35682656" to Pair("Samsung", "Galaxy S23+"),
        "35682456" to Pair("Samsung", "Galaxy S23"),
        "35791433" to Pair("Samsung", "Galaxy S22 Ultra"),
        "35791233" to Pair("Samsung", "Galaxy S22+"),
        "35791033" to Pair("Samsung", "Galaxy S22"),
        "35921444" to Pair("Samsung", "Galaxy Z Fold 5"),
        "35921244" to Pair("Samsung", "Galaxy Z Flip 5"),
        "35359144" to Pair("Samsung", "Galaxy Z Fold 4"),
        "35358944" to Pair("Samsung", "Galaxy Z Flip 4"),
        "35431872" to Pair("Samsung", "Galaxy A55 5G"),
        "35431672" to Pair("Samsung", "Galaxy A35 5G"),
        "35510943" to Pair("Samsung", "Galaxy A54 5G"),

        // OnePlus
        "86129406" to Pair("OnePlus", "OnePlus 12"),
        "86129206" to Pair("OnePlus", "OnePlus 12R"),
        "86348906" to Pair("OnePlus", "OnePlus 11"),
        "86348706" to Pair("OnePlus", "OnePlus 11R"),
        "86542105" to Pair("OnePlus", "OnePlus Open"),
        "86849305" to Pair("OnePlus", "OnePlus 10 Pro"),
        "86849105" to Pair("OnePlus", "OnePlus Nord 3"),

        // Google Pixel
        "35287332" to Pair("Google", "Pixel 8 Pro"),
        "35287132" to Pair("Google", "Pixel 8"),
        "35286932" to Pair("Google", "Pixel 8a"),
        "35412852" to Pair("Google", "Pixel 7 Pro"),
        "35412652" to Pair("Google", "Pixel 7"),
        "35412452" to Pair("Google", "Pixel 7a"),
        "35832731" to Pair("Google", "Pixel 6 Pro"),
        "35832531" to Pair("Google", "Pixel 6"),

        // Xiaomi / Poco / Redmi
        "86948206" to Pair("Xiaomi", "Xiaomi 14 Ultra"),
        "86948006" to Pair("Xiaomi", "Xiaomi 14"),
        "86231906" to Pair("Xiaomi", "Xiaomi 13 Pro"),
        "86231706" to Pair("Poco", "Poco X6 Pro"),
        "86231506" to Pair("Poco", "Poco F5"),
        "86812406" to Pair("Redmi", "Redmi Note 13 Pro+"),

        // Vivo / iQOO
        "86738406" to Pair("Vivo", "Vivo X100 Pro"),
        "86738206" to Pair("Vivo", "Vivo V30 Pro"),
        "86738006" to Pair("iQOO", "iQOO 12"),
        "86737806" to Pair("iQOO", "iQOO Neo 9 Pro"),

        // Realme
        "86493206" to Pair("Realme", "Realme GT 5 Pro"),
        "86493006" to Pair("Realme", "Realme 12 Pro+"),

        // Nothing
        "86319406" to Pair("Nothing", "Phone (2)"),
        "86319206" to Pair("Nothing", "Phone (2a)"),
        "86319006" to Pair("Nothing", "Phone (1)"),
    )

    fun lookup(imei: String): Pair<String, String>? {
        val cleanImei = imei.trim().replace(" ", "")
        if (cleanImei.length < 8) return null
        val tac = cleanImei.substring(0, 8)
        tacMap[tac]?.let { return it }

        // Fallback brand identification from common RBI/GSMA TAC prefixes
        return when {
            tac.startsWith("3529") || tac.startsWith("3536") || tac.startsWith("3530") || tac.startsWith("3544") -> Pair("Apple", "iPhone")
            tac.startsWith("3515") || tac.startsWith("3568") || tac.startsWith("3579") || tac.startsWith("3592") || tac.startsWith("3543") -> Pair("Samsung", "Galaxy")
            tac.startsWith("3528") || tac.startsWith("3541") || tac.startsWith("3583") -> Pair("Google", "Pixel")
            tac.startsWith("8612") || tac.startsWith("8634") || tac.startsWith("8654") -> Pair("OnePlus", "OnePlus")
            tac.startsWith("8694") || tac.startsWith("8623") || tac.startsWith("8681") -> Pair("Xiaomi", "Xiaomi")
            tac.startsWith("8673") -> Pair("Vivo", "Vivo")
            tac.startsWith("8649") -> Pair("Realme", "Realme")
            tac.startsWith("8631") -> Pair("Nothing", "Phone")
            else -> null
        }
    }
}
