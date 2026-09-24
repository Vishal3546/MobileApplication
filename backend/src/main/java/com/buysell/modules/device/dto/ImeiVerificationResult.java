package com.buysell.modules.device.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Response for POST /api/v1/devices/{id}/verify-imei.
 * Mirrors the Android app's ImeiVerificationResultDto
 * (deviceId, state, message, verifiedAt — all string-friendly).
 */
@Data
@Builder
public class ImeiVerificationResult {
    private UUID deviceId;
    private String state;      // e.g. VERIFIED
    private String message;
    private String verifiedAt; // ISO-8601
}
