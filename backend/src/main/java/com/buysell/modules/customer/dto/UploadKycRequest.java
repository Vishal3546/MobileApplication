package com.buysell.modules.customer.dto;

import com.buysell.modules.customer.enums.IdType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UploadKycRequest {
    @NotNull(message = "ID Type is required")
    private IdType idType;

    @NotBlank(message = "ID Number is required")
    private String idNumber;

    private UUID frontMediaId;
    private UUID backMediaId;
    private UUID photoMediaId;
}
