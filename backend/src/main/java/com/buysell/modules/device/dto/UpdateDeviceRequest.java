package com.buysell.modules.device.dto;

import lombok.Data;

@Data
public class UpdateDeviceRequest {
    private String imei2;
    private String serialNumber;
    private String brand;
    private String model;
    private String variant;
    private String color;
    private String storageGb; // Changed to String to support "128 GB" format from frontend
    private String ramGb;     // Changed to String to support "8 GB" format from frontend
}
