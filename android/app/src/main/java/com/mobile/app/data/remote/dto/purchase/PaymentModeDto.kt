package com.mobile.app.data.remote.dto.purchase

import com.google.gson.annotations.SerializedName

enum class PaymentModeDto {
    @SerializedName("CASH") CASH,
    @SerializedName("UPI") UPI,
    @SerializedName("BANK_TRANSFER") BANK_TRANSFER,
    @SerializedName("CARD") CARD,
    @SerializedName("OTHER") OTHER
}
