package com.mobile.app.domain.model.device

object DeviceCatalog {

    val brands = listOf(
        "Apple",
        "Samsung",
        "OnePlus",
        "Google",
        "Xiaomi",
        "Vivo",
        "Realme",
        "Motorola",
        "Nothing",
        "Oppo",
        "iQOO",
        "Poco",
        "Infinix",
        "Techno",
        "Honor",
        "Other"
    )

    val modelsByBrand: Map<String, List<String>> = mapOf(
        "Apple" to listOf(
            "iPhone 15 Pro Max", "iPhone 15 Pro", "iPhone 15 Plus", "iPhone 15",
            "iPhone 14 Pro Max", "iPhone 14 Pro", "iPhone 14 Plus", "iPhone 14",
            "iPhone 13 Pro Max", "iPhone 13 Pro", "iPhone 13", "iPhone 13 mini",
            "iPhone 12 Pro Max", "iPhone 12 Pro", "iPhone 12", "iPhone 12 mini",
            "iPhone 11 Pro Max", "iPhone 11 Pro", "iPhone 11",
            "iPhone SE (3rd Gen)", "iPhone SE (2nd Gen)", "iPhone XR", "iPhone XS Max", "iPhone X"
        ),
        "Samsung" to listOf(
            "Galaxy S24 Ultra", "Galaxy S24+", "Galaxy S24",
            "Galaxy S23 Ultra", "Galaxy S23+", "Galaxy S23", "Galaxy S23 FE",
            "Galaxy S22 Ultra", "Galaxy S22+", "Galaxy S22",
            "Galaxy Z Fold 5", "Galaxy Z Flip 5", "Galaxy Z Fold 4", "Galaxy Z Flip 4",
            "Galaxy A55 5G", "Galaxy A35 5G", "Galaxy A54 5G", "Galaxy A34 5G", "Galaxy A15 5G",
            "Galaxy M34 5G", "Galaxy F54 5G", "Galaxy M14 5G"
        ),
        "OnePlus" to listOf(
            "OnePlus 12", "OnePlus 12R", "OnePlus 11", "OnePlus 11R",
            "OnePlus Open", "OnePlus 10 Pro", "OnePlus 10R", "OnePlus 10T",
            "OnePlus Nord 4", "OnePlus Nord CE 4", "OnePlus Nord 3", "OnePlus Nord CE 3"
        ),
        "Google" to listOf(
            "Pixel 8 Pro", "Pixel 8", "Pixel 8a",
            "Pixel 7 Pro", "Pixel 7", "Pixel 7a",
            "Pixel 6 Pro", "Pixel 6", "Pixel 6a", "Pixel Fold"
        ),
        "Xiaomi" to listOf(
            "Xiaomi 14 Ultra", "Xiaomi 14", "Xiaomi 13 Pro", "Xiaomi 13",
            "Redmi Note 13 Pro+", "Redmi Note 13 Pro", "Redmi Note 13 5G",
            "Redmi 13C 5G", "Redmi Note 12 Pro+", "Redmi Note 12 Pro"
        ),
        "Vivo" to listOf(
            "Vivo X100 Pro", "Vivo X100", "Vivo X90 Pro",
            "Vivo V30 Pro", "Vivo V30", "Vivo V29 Pro", "Vivo V29",
            "Vivo T3 Pro", "Vivo T3 5G", "Vivo T2x 5G"
        ),
        "Realme" to listOf(
            "Realme GT 6", "Realme GT 6T", "Realme GT 2 Pro",
            "Realme 12 Pro+", "Realme 12 Pro", "Realme 12+ 5G",
            "Realme Narzo 70 Pro", "Realme Narzo 60x 5G", "Realme C65 5G"
        ),
        "Motorola" to listOf(
            "Edge 50 Ultra", "Edge 50 Pro", "Edge 50 Fusion",
            "Edge 40 Neo", "Edge 40", "Razr 40 Ultra", "Razr 40",
            "Moto G84 5G", "Moto G64 5G", "Moto G54 5G"
        ),
        "Nothing" to listOf(
            "Phone (2)", "Phone (2a)", "Phone (1)"
        ),
        "iQOO" to listOf(
            "iQOO 12", "iQOO 11", "iQOO Neo 9 Pro", "iQOO Neo 7 Pro",
            "iQOO Z9 5G", "iQOO Z7 Pro"
        ),
        "Poco" to listOf(
            "POCO X6 Pro", "POCO X6", "POCO F6", "POCO M6 Pro 5G", "POCO C65"
        )
    )

    val variants = listOf(
        "Standard / Global",
        "5G Variant",
        "Dual SIM",
        "eSIM / Single SIM",
        "Indian Variant",
        "Special Edition"
    )

    val colors = listOf(
        "Titanium Black", "Titanium Gray", "Titanium Violet", "Natural Titanium",
        "Space Black", "Midnight", "Starlight", "Silver", "Space Gray", "Gold",
        "Phantom Black", "Cream", "Green", "Emerald", "Cyan", "Purple", "Red"
    )

    val storageOptions = listOf(
        "64 GB", "128 GB", "256 GB", "512 GB", "1 TB"
    )

    val ramOptions = listOf(
        "4 GB", "6 GB", "8 GB", "12 GB", "16 GB", "24 GB"
    )
}
