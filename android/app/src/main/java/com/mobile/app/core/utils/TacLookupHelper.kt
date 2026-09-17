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
        "35383009" to Pair("Apple", "iPhone X"),
        "35384109" to Pair("Apple", "iPhone XR"),
        "35383509" to Pair("Apple", "iPhone XS"),
        "35383609" to Pair("Apple", "iPhone XS Max"),

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
        "35178345" to Pair("Samsung", "Galaxy S21 Ultra 5G"),
        "35178145" to Pair("Samsung", "Galaxy S21+ 5G"),
        "35177945" to Pair("Samsung", "Galaxy S21 5G"),
        "35921444" to Pair("Samsung", "Galaxy Z Fold 5"),
        "35921244" to Pair("Samsung", "Galaxy Z Flip 5"),
        "35359144" to Pair("Samsung", "Galaxy Z Fold 4"),
        "35358944" to Pair("Samsung", "Galaxy Z Flip 4"),
        "35431872" to Pair("Samsung", "Galaxy A55 5G"),
        "35431672" to Pair("Samsung", "Galaxy A35 5G"),
        "35510943" to Pair("Samsung", "Galaxy A54 5G"),
        "35327211" to Pair("Samsung", "Galaxy A53 5G"),

        // OnePlus
        "86129406" to Pair("OnePlus", "12"),
        "86129206" to Pair("OnePlus", "12R"),
        "86348906" to Pair("OnePlus", "11"),
        "86348706" to Pair("OnePlus", "11R"),
        "86849305" to Pair("OnePlus", "10 Pro"),
        "86849105" to Pair("OnePlus", "Nord 3"),
        "86154104" to Pair("OnePlus", "9 Pro"),
        "86154204" to Pair("OnePlus", "9"),

        // Google Pixel
        "35287332" to Pair("Google", "Pixel 8 Pro"),
        "35287132" to Pair("Google", "Pixel 8"),
        "35286932" to Pair("Google", "Pixel 8a"),
        "35412852" to Pair("Google", "Pixel 7 Pro"),
        "35412652" to Pair("Google", "Pixel 7"),
        "35412452" to Pair("Google", "Pixel 7a"),
        "35832731" to Pair("Google", "Pixel 6 Pro"),
        "35832531" to Pair("Google", "Pixel 6"),
        "35832331" to Pair("Google", "Pixel 6a"),

        // Xiaomi / Poco / Redmi
        "86948206" to Pair("Xiaomi", "14 Ultra"),
        "86948006" to Pair("Xiaomi", "14"),
        "86231906" to Pair("Xiaomi", "13 Pro"),
        "86231706" to Pair("Poco", "X6 Pro"),
        "86231506" to Pair("Poco", "F5"),
        "86812406" to Pair("Redmi", "Note 13 Pro+"),
        "86812206" to Pair("Redmi", "Note 13 Pro"),

        // Vivo / iQOO
        "86738406" to Pair("Vivo", "X100 Pro"),
        "86738206" to Pair("Vivo", "V30 Pro"),
        "86738006" to Pair("iQOO", "12"),
        "86737806" to Pair("iQOO", "Neo 9 Pro"),
        "86523105" to Pair("Vivo", "X90 Pro"),

        // Realme
        "86493206" to Pair("Realme", "GT 5 Pro"),
        "86493006" to Pair("Realme", "12 Pro+"),
        "86492806" to Pair("Realme", "12 Pro"),

        // Nothing
        "86319406" to Pair("Nothing", "Phone (2)"),
        "86319206" to Pair("Nothing", "Phone (2a)"),
        "86319006" to Pair("Nothing", "Phone (1)")
    )

    fun lookup(imei: String): Pair<String, String>? {
        val cleanImei = imei.trim().replace(" ", "")
        if (cleanImei.length < 8) return null
        val tac = cleanImei.substring(0, 8)
        
        tacMap[tac]?.let { return it }

        // Expanded Fallback
        return when {
            tac.startsWith("3529") || tac.startsWith("3536") || tac.startsWith("3530") || tac.startsWith("3544") || tac.startsWith("3538") -> Pair("Apple", "iPhone")
            tac.startsWith("3515") || tac.startsWith("3568") || tac.startsWith("3579") || tac.startsWith("3592") || tac.startsWith("3543") || tac.startsWith("3517") || tac.startsWith("3532") -> Pair("Samsung", "Galaxy")
            tac.startsWith("3528") || tac.startsWith("3541") || tac.startsWith("3583") -> Pair("Google", "Pixel")
            tac.startsWith("8612") || tac.startsWith("8634") || tac.startsWith("8654") || tac.startsWith("8684") || tac.startsWith("8615") -> Pair("OnePlus", "")
            tac.startsWith("8694") || tac.startsWith("8623") || tac.startsWith("8681") -> Pair("Xiaomi", "Redmi")
            tac.startsWith("8673") || tac.startsWith("8652") -> Pair("Vivo", "")
            tac.startsWith("8649") -> Pair("Realme", "")
            tac.startsWith("8631") -> Pair("Nothing", "Phone")
            else -> null
        }
    }
}
