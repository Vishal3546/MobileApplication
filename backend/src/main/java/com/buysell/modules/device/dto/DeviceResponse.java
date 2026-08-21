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
    private Integer storageGb;
    private Integer ramGb;
    private DeviceStatus status;
    private UUID createdBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
