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
    // The app's device list row renders these — without them every device
    // shows "N/A • N/A RAM / N/A Storage".
    private String variant;
    private String color;
    private String storage;
    private String ram;
}
