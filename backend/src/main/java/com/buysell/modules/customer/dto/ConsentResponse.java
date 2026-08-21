package com.buysell.modules.customer.dto;

import com.buysell.modules.customer.enums.ConsentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConsentResponse {
    private UUID id;
    private UUID customerId;
    private ConsentType consentType;
    private String consentTextVersion;
    private UUID signatureMediaId;
    private UUID videoMediaId;
    private LocalDateTime capturedAt;
}
