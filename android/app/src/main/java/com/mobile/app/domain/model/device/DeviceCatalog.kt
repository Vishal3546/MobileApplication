package com.mobile.app.domain.model.device

data class BrandInfo(
    val name: String,
    val imageUrl: String
)

data class ModelInfo(
    val name: String,
    val imageUrl: String
)

/**
 * Specs of one phone model: RAM/storage variants available in the Indian
 * market plus the most common (default) configuration.
 */
data class ModelSpec(
    val name: String,
    val ramOptions: List<String>,
    val storageOptions: List<String>,
    val defaultRam: String,
    val defaultStorage: String,
)

object DeviceCatalog {

    // Generic placeholder image for models
    private const val SMARTPHONE_PLACEHOLDER =
        "https://images.unsplash.com/photo-1592890288564-76628a30a657?q=80&w=200&auto=format&fit=crop"

    val brands = listOf(
        BrandInfo("Apple", "https://logo.clearbit.com/apple.com"),
        BrandInfo("Samsung", "https://logo.clearbit.com/samsung.com"),
        BrandInfo("OnePlus", "https://logo.clearbit.com/oneplus.com"),
        BrandInfo("Google", "https://logo.clearbit.com/google.com"),
        BrandInfo("Xiaomi", "https://logo.clearbit.com/mi.com"),
        BrandInfo("Vivo", "https://logo.clearbit.com/vivo.com"),
        BrandInfo("Realme", "https://logo.clearbit.com/realme.com"),
        BrandInfo("Motorola", "https://logo.clearbit.com/motorola.com"),
        BrandInfo("Nothing", "https://logo.clearbit.com/nothing.tech"),
        BrandInfo("Oppo", "https://logo.clearbit.com/oppo.com"),
        BrandInfo("iQOO", "https://logo.clearbit.com/iqoo.com"),
        BrandInfo("Poco", "https://logo.clearbit.com/po.co"),
        BrandInfo("Infinix", "https://logo.clearbit.com/infinixmobility.com"),
        BrandInfo("Honor", "https://logo.clearbit.com/hihonor.com")
    )

    // Compact builder: default RAM = 2nd option if present (the common
    // mid variant), default storage = base option.
    private fun m(name: String, ram: List<String>, storage: List<String>) = ModelSpec(
        name = name,
        ramOptions = ram,
        storageOptions = storage,
        defaultRam = ram.getOrElse(1) { ram.first() },
        defaultStorage = storage.first(),
    )

    private val g = " GB"
    private val t = " TB"

    val modelSpecsByBrand: Map<String, List<ModelSpec>> = mapOf(

        "Apple" to listOf(
            m("iPhone 15 Pro Max", listOf("8$g"), listOf("256$g", "512$g", "1$t")),
            m("iPhone 15 Pro", listOf("8$g"), listOf("128$g", "256$g", "512$g", "1$t")),
            m("iPhone 15 Plus", listOf("6$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 15", listOf("6$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 14 Pro Max", listOf("6$g"), listOf("128$g", "256$g", "512$g", "1$t")),
            m("iPhone 14 Pro", listOf("6$g"), listOf("128$g", "256$g", "512$g", "1$t")),
            m("iPhone 14 Plus", listOf("6$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 14", listOf("6$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 13 Pro Max", listOf("6$g"), listOf("128$g", "256$g", "512$g", "1$t")),
            m("iPhone 13 Pro", listOf("6$g"), listOf("128$g", "256$g", "512$g", "1$t")),
            m("iPhone 13", listOf("4$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 13 mini", listOf("4$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 12 Pro Max", listOf("6$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 12 Pro", listOf("6$g"), listOf("128$g", "256$g", "512$g")),
            m("iPhone 12", listOf("4$g"), listOf("64$g", "128$g", "256$g")),
            m("iPhone 12 mini", listOf("4$g"), listOf("64$g", "128$g", "256$g")),
            m("iPhone 11 Pro Max", listOf("4$g"), listOf("64$g", "256$g", "512$g")),
            m("iPhone 11 Pro", listOf("4$g"), listOf("64$g", "256$g", "512$g")),
            m("iPhone 11", listOf("4$g"), listOf("64$g", "128$g")),
            m("iPhone SE (2022)", listOf("4$g"), listOf("64$g", "128$g", "256$g")),
            m("iPhone SE (2020)", listOf("3$g"), listOf("64$g", "128$g", "256$g")),
            m("iPhone XR", listOf("3$g"), listOf("64$g", "128$g")),
            m("iPhone XS Max", listOf("4$g"), listOf("64$g", "256$g", "512$g")),
            m("iPhone XS", listOf("4$g"), listOf("64$g", "256$g", "512$g")),
        ),

        "Samsung" to listOf(
            m("Galaxy S24 Ultra", listOf("12$g"), listOf("256$g", "512$g", "1$t")),
            m("Galaxy S24+", listOf("12$g"), listOf("256$g", "512$g")),
            m("Galaxy S24", listOf("8$g"), listOf("128$g", "256$g")),
            m("Galaxy S23 Ultra", listOf("12$g"), listOf("256$g", "512$g")),
            m("Galaxy S23", listOf("8$g"), listOf("128$g", "256$g")),
            m("Galaxy S22 Ultra", listOf("12$g"), listOf("256$g", "512$g")),
            m("Galaxy S22", listOf("8$g"), listOf("128$g", "256$g")),
            m("Galaxy S21 Ultra 5G", listOf("12$g", "16$g"), listOf("128$g", "256$g", "512$g")),
            m("Galaxy S21 FE 5G", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Galaxy Z Fold 5", listOf("12$g"), listOf("256$g", "512$g")),
            m("Galaxy Z Flip 5", listOf("8$g"), listOf("256$g", "512$g")),
            m("Galaxy A55 5G", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("Galaxy A54 5G", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Galaxy A53 5G", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Galaxy A35 5G", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Galaxy A34 5G", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Galaxy A23", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("Galaxy A14", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("Galaxy M34 5G", listOf("6$g", "8$g"), listOf("128$g")),
            m("Galaxy M14 5G", listOf("4$g", "6$g"), listOf("128$g")),
            m("Galaxy F14 5G", listOf("4$g", "6$g"), listOf("128$g")),
            m("Galaxy M32", listOf("6$g", "8$g"), listOf("64$g", "128$g")),
        ),

        "OnePlus" to listOf(
            m("12", listOf("12$g", "16$g"), listOf("256$g", "512$g")),
            m("12R", listOf("8$g", "16$g"), listOf("128$g", "256$g")),
            m("11", listOf("8$g", "16$g"), listOf("128$g", "256$g")),
            m("11R", listOf("8$g", "16$g"), listOf("128$g", "256$g")),
            m("10 Pro", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("10R", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("9 Pro", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("9", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("8T", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("Nord 3", listOf("8$g", "16$g"), listOf("128$g", "256$g")),
            m("Nord 2", listOf("6$g", "8$g", "12$g"), listOf("128$g", "256$g")),
            m("Nord CE 4", listOf("8$g"), listOf("128$g", "256$g")),
            m("Nord CE 3 Lite", listOf("8$g"), listOf("128$g", "256$g")),
            m("Nord CE 2", listOf("6$g", "8$g"), listOf("128$g")),
        ),

        "Google" to listOf(
            m("Pixel 9 Pro XL", listOf("16$g"), listOf("256$g", "512$g")),
            m("Pixel 9 Pro", listOf("16$g"), listOf("128$g", "256$g", "512$g")),
            m("Pixel 9", listOf("12$g"), listOf("128$g", "256$g")),
            m("Pixel 9a", listOf("8$g"), listOf("128$g", "256$g")),
            m("Pixel 8 Pro", listOf("12$g"), listOf("128$g", "256$g", "512$g", "1$t")),
            m("Pixel 8", listOf("8$g"), listOf("128$g", "256$g")),
            m("Pixel 8a", listOf("8$g"), listOf("128$g", "256$g")),
            m("Pixel 7 Pro", listOf("12$g"), listOf("128$g", "256$g", "512$g")),
            m("Pixel 7", listOf("8$g"), listOf("128$g", "256$g")),
            m("Pixel 7a", listOf("8$g"), listOf("128$g")),
            m("Pixel 6 Pro", listOf("12$g"), listOf("128$g", "256$g", "512$g")),
            m("Pixel 6", listOf("8$g"), listOf("128$g", "256$g")),
            m("Pixel 6a", listOf("6$g"), listOf("128$g")),
        ),

        "Xiaomi" to listOf(
            m("Xiaomi 14 Ultra", listOf("12$g", "16$g"), listOf("256$g", "512$g")),
            m("Xiaomi 14", listOf("12$g"), listOf("256$g", "512$g")),
            m("Xiaomi 13 Pro", listOf("12$g"), listOf("256$g")),
            m("Xiaomi 12 Pro", listOf("8$g", "12$g"), listOf("256$g")),
            m("Xiaomi 11i", listOf("8$g"), listOf("128$g", "256$g")),
            m("Mi 11X Pro", listOf("8$g"), listOf("256$g")),
            m("Mi 11X", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Redmi Note 13 Pro+", listOf("8$g", "12$g"), listOf("256$g", "512$g")),
            m("Redmi Note 13 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("Redmi Note 13", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Redmi Note 12 Pro+", listOf("8$g", "12$g"), listOf("256$g")),
            m("Redmi Note 12 Pro", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("Redmi Note 12", listOf("4$g", "6$g"), listOf("128$g")),
            m("Redmi Note 11 Pro+", listOf("6$g", "8$g"), listOf("128$g")),
            m("Redmi Note 11", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("Redmi Note 10 Pro", listOf("6$g", "8$g"), listOf("64$g", "128$g", "256$g")),
            m("Redmi 13C", listOf("4$g", "6$g", "8$g"), listOf("128$g", "256$g")),
            m("Redmi 12 5G", listOf("4$g", "6$g", "8$g"), listOf("128$g")),
            m("Redmi 12C", listOf("3$g", "4$g", "6$g"), listOf("32$g", "64$g", "128$g")),
            m("Redmi 11 Prime", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("Redmi 10", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("Redmi 9A", listOf("2$g", "3$g", "4$g"), listOf("32$g", "64$g")),
        ),

        "Poco" to listOf(
            m("X6 Pro", listOf("8$g", "12$g"), listOf("256$g", "512$g")),
            m("X6", listOf("8$g", "12$g"), listOf("256$g")),
            m("X5 Pro", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("F5", listOf("8$g", "12$g"), listOf("256$g")),
            m("F4", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("M6 Pro", listOf("8$g", "12$g"), listOf("256$g")),
            m("M4 Pro", listOf("6$g", "8$g"), listOf("128$g")),
            m("C65", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("X3 Pro", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("F1", listOf("6$g", "8$g"), listOf("64$g", "128$g", "256$g")),
        ),

        "Vivo" to listOf(
            m("X100 Pro", listOf("16$g"), listOf("512$g")),
            m("X100", listOf("12$g", "16$g"), listOf("256$g", "512$g")),
            m("V30 Pro", listOf("12$g"), listOf("256$g")),
            m("V30", listOf("8$g"), listOf("128$g", "256$g")),
            m("V29 Pro", listOf("8$g"), listOf("256$g")),
            m("V29", listOf("8$g"), listOf("128$g", "256$g")),
            m("V27 Pro", listOf("8$g"), listOf("256$g")),
            m("V27", listOf("8$g"), listOf("128$g", "256$g")),
            m("V25 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("V23", listOf("8$g"), listOf("128$g", "256$g")),
            m("T3 5G", listOf("8$g"), listOf("128$g", "256$g")),
            m("T2 5G", listOf("6$g", "8$g"), listOf("128$g")),
            m("Y200", listOf("8$g"), listOf("128$g", "256$g")),
            m("Y100", listOf("8$g"), listOf("128$g", "256$g")),
            m("Y56", listOf("4$g", "8$g"), listOf("128$g")),
            m("Y17s", listOf("4$g", "6$g"), listOf("128$g")),
            m("Y16", listOf("3$g", "4$g"), listOf("64$g", "128$g")),
            m("Y21", listOf("4$g"), listOf("64$g", "128$g")),
        ),

        "Realme" to listOf(
            m("GT 6", listOf("8$g", "12$g"), listOf("256$g", "512$g")),
            m("GT 2 Pro", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("GT Neo 3", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("12 Pro+", listOf("8$g", "12$g"), listOf("256$g")),
            m("12 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("12+", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
            m("11 Pro+", listOf("8$g", "12$g"), listOf("256$g")),
            m("11 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("11x 5G", listOf("6$g", "8$g"), listOf("128$g")),
            m("Narzo 70 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("Narzo 60 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("Narzo 50", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("C67", listOf("6$g", "8$g"), listOf("128$g")),
            m("C55", listOf("6$g", "8$g"), listOf("64$g", "128$g")),
            m("C53", listOf("6$g", "8$g"), listOf("128$g")),
            m("C33", listOf("3$g", "4$g"), listOf("32$g", "64$g")),
        ),

        "Motorola" to listOf(
            m("Edge 50 Pro", listOf("8$g", "12$g"), listOf("256$g")),
            m("Edge 40", listOf("8$g"), listOf("256$g")),
            m("Edge 30 Pro", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("Moto G84 5G", listOf("12$g"), listOf("256$g")),
            m("Moto G64 5G", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("Moto G45 5G", listOf("8$g"), listOf("128$g")),
            m("Moto G34 5G", listOf("8$g"), listOf("128$g")),
            m("Moto G73 5G", listOf("8$g"), listOf("256$g")),
            m("Moto G52", listOf("6$g", "8$g"), listOf("128$g")),
            m("Moto G32", listOf("8$g"), listOf("128$g")),
            m("Moto G14", listOf("4$g", "8$g"), listOf("128$g")),
            m("Moto Razr 40", listOf("8$g"), listOf("128$g", "256$g")),
        ),

        "Nothing" to listOf(
            m("Phone (3)", listOf("12$g"), listOf("256$g", "512$g")),
            m("Phone (3a)", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("Phone (3a) Pro", listOf("8$g", "12$g"), listOf("256$g")),
            m("Phone (2a) Plus", listOf("12$g"), listOf("256$g")),
            m("Phone (2a)", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("Phone (2)", listOf("8$g", "12$g"), listOf("128$g", "256$g", "512$g")),
            m("Phone (1)", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
            m("CMF Phone 1", listOf("6$g", "8$g"), listOf("128$g", "256$g")),
        ),

        "Oppo" to listOf(
            m("Reno 12 Pro", listOf("12$g"), listOf("256$g")),
            m("Reno 12", listOf("8$g", "12$g"), listOf("256$g")),
            m("Reno 11 Pro", listOf("12$g"), listOf("256$g")),
            m("Reno 11", listOf("8$g"), listOf("128$g", "256$g")),
            m("Reno 10 Pro", listOf("12$g"), listOf("256$g")),
            m("Reno 10", listOf("8$g"), listOf("128$g", "256$g")),
            m("F25 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("F23 5G", listOf("8$g"), listOf("128$g")),
            m("A78", listOf("8$g"), listOf("128$g", "256$g")),
            m("A58", listOf("6$g", "8$g"), listOf("128$g")),
            m("A54", listOf("4$g", "6$g"), listOf("64$g", "128$g")),
            m("K10 5G", listOf("6$g", "8$g"), listOf("128$g")),
        ),

        "iQOO" to listOf(
            m("12", listOf("12$g"), listOf("256$g", "512$g")),
            m("11", listOf("8$g", "12$g", "16$g"), listOf("256$g")),
            m("Neo 9 Pro", listOf("8$g", "12$g"), listOf("128$g", "256$g", "512$g")),
            m("Neo 9", listOf("8$g", "12$g"), listOf("256$g")),
            m("Z9 5G", listOf("8$g"), listOf("128$g", "256$g")),
            m("Z9x", listOf("8$g"), listOf("128$g")),
            m("Z7 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("Z6 Pro", listOf("8$g"), listOf("128$g", "256$g")),
            m("9 Pro", listOf("8$g", "12$g"), listOf("256$g")),
            m("Neo 7 Pro", listOf("8$g", "12$g"), listOf("128$g", "256$g")),
        ),

        "Infinix" to listOf(
            m("Note 40 Pro", listOf("8$g", "12$g"), listOf("256$g")),
            m("Note 30", listOf("8$g"), listOf("128$g", "256$g")),
            m("Hot 40i", listOf("8$g"), listOf("256$g")),
            m("Hot 30i", listOf("4$g", "8$g"), listOf("64$g", "128$g")),
            m("Smart 8", listOf("4$g", "8$g"), listOf("64$g", "128$g")),
            m("Zero 30", listOf("8$g", "12$g"), listOf("256$g")),
            m("GT 20 Pro", listOf("8$g", "12$g"), listOf("256$g")),
        ),

        "Honor" to listOf(
            m("Magic 6 Pro", listOf("12$g", "16$g"), listOf("512$g")),
            m("200 Pro", listOf("12$g"), listOf("256$g", "512$g")),
            m("200", listOf("8$g", "12$g"), listOf("256$g")),
            m("90", listOf("8$g", "12$g"), listOf("256$g", "512$g")),
            m("X9b", listOf("8$g"), listOf("128$g", "256$g")),
            m("X8b", listOf("8$g"), listOf("128$g", "256$g")),
            m("X7b", listOf("6$g", "8$g"), listOf("128$g")),
        ),
    )

    /** Grid-friendly model list derived from the spec database. */
    val modelsByBrand: Map<String, List<ModelInfo>> =
        modelSpecsByBrand.mapValues { (_, specs) ->
            specs.map { ModelInfo(it.name, SMARTPHONE_PLACEHOLDER) }
        }

    /**
     * Look up specs for a model. Exact (case-insensitive) name match first,
     * then a contains-match so slightly different naming still resolves.
     */
    fun specFor(brand: String, model: String): ModelSpec? {
        val specs = modelSpecsByBrand[brand] ?: return null
        return specs.firstOrNull { it.name.equals(model.trim(), ignoreCase = true) }
            ?: specs.firstOrNull {
                it.name.contains(model.trim(), ignoreCase = true) ||
                    model.trim().contains(it.name, ignoreCase = true)
            }
    }

    val variants = listOf("Standard", "5G", "Dual SIM", "Global")
    val colors = listOf("Black", "White", "Blue", "Titanium", "Gold", "Green", "Silver", "Grey", "Purple", "Red")

    // Global fallbacks (used when the selected model has no spec entry)
    val ramOptions = listOf("2 GB", "3 GB", "4 GB", "6 GB", "8 GB", "12 GB", "16 GB")
    val storageOptions = listOf("16 GB", "32 GB", "64 GB", "128 GB", "256 GB", "512 GB", "1 TB")
}
