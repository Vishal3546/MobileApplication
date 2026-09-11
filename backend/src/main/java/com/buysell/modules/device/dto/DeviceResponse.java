package com.buysell.modules.device.dto;

import com.buysell.modules.device.enums.DeviceStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class DeviceResponse {
    private UUID id;
    private String imei1;
    private String imei2;
    private String serialNumber;
    private String brand;
    private String model;
    private String variant;
    private String color;
    private String storage; // Changed to String for frontend compatibility
    private String ram;     // Changed to String for frontend compatibility
    private DeviceStatus status;
    private UUID branchId;
    private String verificationState;
    private Integer mediaCount;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
