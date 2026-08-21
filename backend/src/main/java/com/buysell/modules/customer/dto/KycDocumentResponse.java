package com.buysell.modules.customer.dto;

import com.buysell.modules.customer.enums.IdType;
import com.buysell.modules.customer.enums.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class KycDocumentResponse {
    private UUID id;
    private UUID customerId;
    private IdType idType;
    private String idNumberMasked; // Do not return raw/encrypted id
    private UUID frontMediaId;
    private UUID backMediaId;
    private UUID photoMediaId;
    private VerificationStatus verificationStatus;
    private String verificationNotes;
    private LocalDateTime verifiedAt;
}
