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

    val modelsByBrand: Map<String, List<ModelInfo>> = emptyMap()

    val variants = listOf("Standard", "5G", "Dual SIM", "Global")
    val colors = listOf("Black", "White", "Blue", "Titanium", "Gold", "Green")
    val storageOptions = listOf("128 GB", "256 GB", "512 GB", "1 TB")
    val ramOptions = listOf("8 GB", "12 GB", "16 GB")
}
