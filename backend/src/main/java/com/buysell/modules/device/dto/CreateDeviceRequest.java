package com.buysell.modules.device.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateDeviceRequest {
    
    @NotBlank(message = "IMEI1 is required")
    @Size(min = 15, max = 15, message = "IMEI must be exactly 15 digits")
    private String imei1;
    
    private String imei2;
    private String serialNumber;
    
    @NotBlank(message = "Brand is required")
    private String brand;
    
    @NotBlank(message = "Model is required")
    private String model;
    
    private String variant;
    private String color;
    private Integer storageGb;
    private Integer ramGb;
}
