package com.buysell.modules.device.dto;

import com.buysell.modules.device.enums.DeviceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDeviceStatusRequest {
    @NotNull(message = "Status is required")
    private DeviceStatus status;
}
