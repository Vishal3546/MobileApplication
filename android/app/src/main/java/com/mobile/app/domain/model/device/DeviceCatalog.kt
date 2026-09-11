package com.mobile.app.domain.model.device

data class BrandInfo(
    val name: String,
    val imageUrl: String
)

data class ModelInfo(
    val name: String,
    val imageUrl: String
)

object DeviceCatalog {

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

    // Using generic placeholder images for models - in production these would be real device shots
    private const val SMARTPHONE_PLACEHOLDER = "https://images.unsplash.com/photo-1592890288564-76628a30a657?q=80&w=200&auto=format&fit=crop"

    val modelsByBrand: Map<String, List<ModelInfo>> = mapOf(
        "Apple" to listOf(
            ModelInfo("iPhone 15 Pro Max", "https://fdn2.gsmarena.com/videolib/cp/apple-iphone-15-pro-max.jpg"),
            ModelInfo("iPhone 15 Pro", "https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-15-pro.jpg"),
            ModelInfo("iPhone 14 Pro Max", "https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-14-pro-max.jpg"),
            ModelInfo("iPhone 13", "https://fdn2.gsmarena.com/vv/bigpic/apple-iphone-13.jpg")
        ),
        "Samsung" to listOf(
            ModelInfo("Galaxy S24 Ultra", "https://fdn2.gsmarena.com/vv/bigpic/samsung-galaxy-s24-ultra-5g.jpg"),
            ModelInfo("Galaxy Z Fold 5", "https://fdn2.gsmarena.com/vv/bigpic/samsung-galaxy-z-fold5.jpg"),
            ModelInfo("Galaxy A55", "https://fdn2.gsmarena.com/vv/bigpic/samsung-galaxy-a55.jpg")
        ),
        "OnePlus" to listOf(
            ModelInfo("OnePlus 12", "https://fdn2.gsmarena.com/vv/bigpic/oneplus-12.jpg"),
            ModelInfo("OnePlus Open", "https://fdn2.gsmarena.com/vv/bigpic/oneplus-open.jpg")
        ),
        "Google" to listOf(
            ModelInfo("Pixel 8 Pro", "https://fdn2.gsmarena.com/vv/bigpic/google-pixel-8-pro.jpg"),
            ModelInfo("Pixel 7a", "https://fdn2.gsmarena.com/vv/bigpic/google-pixel-7a.jpg")
        )
    )

    val variants = listOf("Standard", "5G", "Dual SIM", "Global")
    val colors = listOf("Black", "White", "Blue", "Titanium", "Gold", "Green")
    val storageOptions = listOf("128 GB", "256 GB", "512 GB", "1 TB")
    val ramOptions = listOf("8 GB", "12 GB", "16 GB")
}
