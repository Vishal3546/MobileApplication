package com.buysell.modules.customer.dto;

import com.buysell.modules.customer.enums.CustomerStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateCustomerRequest extends CreateCustomerRequest {
    private CustomerStatus status;
}
