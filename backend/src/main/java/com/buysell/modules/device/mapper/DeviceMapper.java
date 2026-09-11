package com.buysell.modules.device.mapper;

import com.buysell.modules.device.dto.CreateDeviceRequest;
import com.buysell.modules.device.dto.DeviceResponse;
import com.buysell.modules.device.dto.DeviceSummaryResponse;
import com.buysell.modules.device.entity.Device;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
                .storageGb(DeviceMapper.parseSize(request.getStorageGb()))
                .ramGb(DeviceMapper.parseSize(request.getRamGb()))
                .build();
    }

    public static Integer parseSize(String val) {
        if (val == null) return null;
        try {
            return Integer.parseInt(val.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return null;
        }
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
                .storage(device.getStorageGb() != null ? device.getStorageGb() + " GB" : "N/A")
                .ram(device.getRamGb() != null ? device.getRamGb() + " GB" : "N/A")
                .status(device.getStatus())
                .verificationState("VERIFIED") // Default for now
                .mediaCount(0) // Placeholder
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

        if (imei.length() <= 4) {
            return "****";
        }
        
        return "*".repeat(imei.length() - 4) + imei.substring(imei.length() - 4);
    }
}
