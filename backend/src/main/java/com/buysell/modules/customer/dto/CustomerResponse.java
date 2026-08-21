package com.buysell.modules.customer.dto;

import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String phone;
    private String altPhone;
    private String email;
    private String address;
    private CustomerStatus status;
    private VerificationStatus verificationStatus;
}
