package com.buysell.modules.device.dto;

import com.buysell.modules.device.enums.DeviceStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeviceSummaryResponse {
    private UUID id;
    private String imei1;
    private String brand;
    private String model;
    private DeviceStatus status;
}
