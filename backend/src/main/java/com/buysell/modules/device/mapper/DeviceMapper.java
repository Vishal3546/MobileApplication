package com.buysell.modules.device.mapper;

import com.buysell.modules.device.dto.CreateDeviceRequest;
import com.buysell.modules.device.dto.DeviceResponse;
import com.buysell.modules.device.dto.DeviceSummaryResponse;
import com.buysell.modules.device.entity.Device;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceMapper {

    private final CurrentUserService currentUserService;

    public Device toEntity(CreateDeviceRequest request) {
        if (request == null) return null;
        
        return Device.builder()
                .imei1(request.getImei1())
                .imei2(request.getImei2())
                .serialNumber(request.getSerialNumber())
                .brand(request.getBrand())
                .model(request.getModel())
                .variant(request.getVariant())
                .color(request.getColor())
                .storageGb(request.getStorageGb())
                .ramGb(request.getRamGb())
                .build();
    }

    public DeviceResponse toResponse(Device device) {
        if (device == null) return null;

        boolean canViewFullImei = currentUserService.hasPermission("VIEW_FULL_DEVICE_IMEI");

        return DeviceResponse.builder()
                .id(device.getId())
                .imei1(maskImeiIfNeeded(device.getImei1(), canViewFullImei))
                .imei2(maskImeiIfNeeded(device.getImei2(), canViewFullImei))
                .serialNumber(device.getSerialNumber())
                .brand(device.getBrand())
                .model(device.getModel())
                .variant(device.getVariant())
                .color(device.getColor())
                .storageGb(device.getStorageGb())
                .ramGb(device.getRamGb())
                .status(device.getStatus())
                .createdBy(device.getCreatedBy() != null ? device.getCreatedBy().getId() : null)
                .createdAt(device.getCreatedAt())
                .updatedAt(device.getUpdatedAt())
                .build();
    }

    public DeviceSummaryResponse toSummaryResponse(Device device) {
        if (device == null) return null;

        boolean canViewFullImei = currentUserService.hasPermission("VIEW_FULL_DEVICE_IMEI");

        return DeviceSummaryResponse.builder()
                .id(device.getId())
                .imei1(maskImeiIfNeeded(device.getImei1(), canViewFullImei))
                .brand(device.getBrand())
                .model(device.getModel())
                .status(device.getStatus())
                .build();
    }

    private String maskImeiIfNeeded(String imei, boolean canViewFull) {
        if (imei == null || imei.isEmpty()) return imei;
        if (canViewFull) return imei;

        // Masking: ***********3809 (keep last 4 digits)
        if (imei.length() <= 4) {
            return "****"; // Edge case
        }
        
        return "*".repeat(imei.length() - 4) + imei.substring(imei.length() - 4);
    }
}
