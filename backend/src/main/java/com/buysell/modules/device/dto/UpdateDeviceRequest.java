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
    private Integer storageGb;
    private Integer ramGb;
}
