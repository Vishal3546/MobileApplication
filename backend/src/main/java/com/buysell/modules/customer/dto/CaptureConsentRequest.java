package com.buysell.modules.customer.dto;

import com.buysell.modules.customer.enums.ConsentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CaptureConsentRequest {
    @NotNull(message = "Consent Type is required")
    private ConsentType consentType;

    @NotBlank(message = "Consent Text Version is required")
    private String consentTextVersion;

    private UUID signatureMediaId;
    private UUID videoMediaId;
    
    private String ipAddress;
    private String deviceInfo;
}
