package com.mobile.app.data.mapper

import com.mobile.app.data.remote.dto.CustomerDto
import com.mobile.app.domain.enums.CustomerStatus
import com.mobile.app.domain.enums.VerificationStatus
import com.mobile.app.domain.model.Customer

fun CustomerDto.toDomain(): Customer {
    return Customer(
        id = id,
        firstName = firstName,
        lastName = lastName,
        phone = phone,
        altPhone = altPhone,
        email = email,
        address = address,
        status = runCatching { CustomerStatus.valueOf(status) }.getOrDefault(CustomerStatus.INACTIVE),
        verificationStatus = runCatching { VerificationStatus.valueOf(verificationStatus) }.getOrDefault(VerificationStatus.NOT_STARTED)
    )
}
