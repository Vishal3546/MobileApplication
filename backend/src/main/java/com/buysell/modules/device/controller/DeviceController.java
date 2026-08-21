package com.buysell.modules.device.controller;

import com.buysell.modules.device.dto.*;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.entity.DeviceCondition;
import com.buysell.modules.device.entity.DeviceInspection;
import com.buysell.modules.device.entity.DeviceMedia;
import com.buysell.modules.device.enums.DeviceViewType;
import com.buysell.modules.device.mapper.DeviceMapper;
import com.buysell.modules.device.service.DeviceConditionService;
import com.buysell.modules.device.service.DeviceInspectionService;
import com.buysell.modules.device.service.DeviceMediaService;
import com.buysell.modules.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Tag(name = "Device Management", description = "APIs for managing mobile devices")
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceConditionService conditionService;
    private final DeviceInspectionService inspectionService;
    private final DeviceMediaService deviceMediaService;
    private final DeviceMapper deviceMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_DEVICE')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new device")
    public DeviceResponse createDevice(@Valid @RequestBody CreateDeviceRequest request) {
        Device device = deviceService.createDevice(request);
        return deviceMapper.toResponse(device);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_DEVICE')")
    @Operation(summary = "Update an existing device")
    public DeviceResponse updateDevice(@PathVariable UUID id, @Valid @RequestBody UpdateDeviceRequest request) {
        Device device = deviceService.updateDevice(id, request);
        return deviceMapper.toResponse(device);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_DEVICES')")
    @Operation(summary = "Get a device by ID")
    public DeviceResponse getDevice(@PathVariable UUID id) {
        Device device = deviceService.getDeviceById(id);
        return deviceMapper.toResponse(device);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_DEVICES')")
    @Operation(summary = "Search devices with pagination")
    public Page<DeviceSummaryResponse> searchDevices(
            @RequestParam(required = false) String imei,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            Pageable pageable) {
        return deviceService.searchDevices(imei, brand, model, pageable)
                .map(deviceMapper::toSummaryResponse);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('BLOCK_DEVICE')")
    @Operation(summary = "Block or unblock a device")
    public void updateDeviceStatus(@PathVariable UUID id, @Valid @RequestBody UpdateDeviceStatusRequest request) {
        deviceService.updateDeviceStatus(id, request.getStatus());
    }

    @PostMapping("/{id}/verify-imei")
    @PreAuthorize("hasAuthority('UPDATE_DEVICE')")
    @Operation(summary = "Manually trigger IMEI verification event")
    public void verifyImei(@PathVariable UUID id) {
        deviceService.recordImeiVerification(id);
    }

    @PostMapping("/{id}/conditions")
    @PreAuthorize("hasAuthority('MANAGE_DEVICE_CONDITION')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a new device condition snapshot")
    public DeviceCondition recordCondition(@PathVariable UUID id, @Valid @RequestBody DeviceCondition condition) {
        return conditionService.recordCondition(id, condition);
    }

    @GetMapping("/{id}/conditions/history")
    @PreAuthorize("hasAuthority('VIEW_DEVICES')")
    @Operation(summary = "Get device condition history")
    public List<DeviceCondition> getConditionHistory(@PathVariable UUID id) {
        return conditionService.getConditionHistory(id);
    }

    @PostMapping("/{id}/inspections")
    @PreAuthorize("hasAuthority('CREATE_DEVICE_INSPECTION')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Record a new device inspection")
    public DeviceInspection recordInspection(@PathVariable UUID id, @Valid @RequestBody DeviceInspection inspection) {
        return inspectionService.recordInspection(id, inspection);
    }

    @GetMapping("/{id}/inspections/history")
    @PreAuthorize("hasAuthority('VIEW_DEVICE_INSPECTION')")
    @Operation(summary = "Get device inspection history")
    public List<DeviceInspection> getInspectionHistory(@PathVariable UUID id) {
        return inspectionService.getInspectionHistory(id);
    }

    @PostMapping("/{id}/media/{mediaId}")
    @PreAuthorize("hasAuthority('UPLOAD_DEVICE_MEDIA')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Attach an existing media file to a device")
    public DeviceMedia addMedia(@PathVariable UUID id, @PathVariable UUID mediaId, @RequestParam DeviceViewType viewType) {
        return deviceMediaService.addMedia(id, mediaId, viewType);
    }

    @GetMapping("/{id}/media")
    @PreAuthorize("hasAuthority('VIEW_DEVICE_MEDIA')")
    @Operation(summary = "Get device media links")
    public List<DeviceMedia> getDeviceMedia(@PathVariable UUID id) {
        return deviceMediaService.getDeviceMedia(id);
    }

    @GetMapping("/{id}/lifecycle")
    @PreAuthorize("hasAuthority('VIEW_DEVICE_LIFECYCLE')")
    @Operation(summary = "Get device lifecycle history")
    public List<com.buysell.modules.device.entity.DeviceLifecycleHistory> getDeviceLifecycle(@PathVariable UUID id) {
        return deviceService.getDeviceLifecycleHistory(id);
    }
}
