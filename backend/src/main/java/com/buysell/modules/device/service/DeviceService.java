package com.buysell.modules.device.service;

import com.buysell.exception.BusinessException;
import com.buysell.exception.ResourceNotFoundException;
import com.buysell.modules.device.dto.CreateDeviceRequest;
import com.buysell.modules.device.dto.UpdateDeviceRequest;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.enums.DeviceStatus;
import com.buysell.modules.device.enums.LifecycleEventType;
import com.buysell.modules.device.repository.DeviceRepository;
import com.buysell.modules.device.service.provider.ImeiVerificationProvider;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceLifecycleService lifecycleService;
    private final CurrentUserService currentUserService;
    private final ImeiVerificationProvider imeiVerificationProvider;

    @Transactional
    public Device createDevice(CreateDeviceRequest request) {
        // Validate and normalize IMEIs
        String imei1 = imeiVerificationProvider.verifyImei(request.getImei1(), "IMEI1");
        String imei2 = null;
        if (request.getImei2() != null && !request.getImei2().isBlank()) {
            imei2 = imeiVerificationProvider.verifyImei(request.getImei2(), "IMEI2");
            if (imei1.equals(imei2)) {
                throw new BusinessException("INVALID_IMEI", "IMEI1 and IMEI2 cannot be the same.", HttpStatus.BAD_REQUEST);
            }
        }

        User currentUser = currentUserService.getCurrentUser();

        Device device = Device.builder()
                .imei1(imei1)
                .imei2(imei2)
                .serialNumber(request.getSerialNumber())
                .brand(request.getBrand())
                .model(request.getModel())
                .variant(request.getVariant())
                .color(request.getColor())
                .storageGb(request.getStorageGb())
                .ramGb(request.getRamGb())
                .status(DeviceStatus.ACTIVE)
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        device = deviceRepository.save(device);

        lifecycleService.recordEvent(device, LifecycleEventType.DEVICE_CREATED, null, "Device registered in system");

        return device;
    }

    @Transactional
    public Device updateDevice(UUID id, UpdateDeviceRequest request) {
        Device device = getDeviceById(id);

        if (request.getImei2() != null && !request.getImei2().isBlank()) {
            String imei2 = imeiVerificationProvider.verifyImei(request.getImei2(), "IMEI2", id);
            if (device.getImei1().equals(imei2)) {
                throw new BusinessException("INVALID_IMEI", "IMEI1 and IMEI2 cannot be the same.", HttpStatus.BAD_REQUEST);
            }
            device.setImei2(imei2);
        }

        if (request.getSerialNumber() != null) device.setSerialNumber(request.getSerialNumber());
        if (request.getBrand() != null) device.setBrand(request.getBrand());
        if (request.getModel() != null) device.setModel(request.getModel());
        if (request.getVariant() != null) device.setVariant(request.getVariant());
        if (request.getColor() != null) device.setColor(request.getColor());
        if (request.getStorageGb() != null) device.setStorageGb(request.getStorageGb());
        if (request.getRamGb() != null) device.setRamGb(request.getRamGb());

        device.setUpdatedBy(currentUserService.getCurrentUser());
        
        device = deviceRepository.save(device);
        
        lifecycleService.recordEvent(device, LifecycleEventType.DEVICE_UPDATED, null, "Device details updated");
        
        return device;
    }

    @Transactional(readOnly = true)
    public Device getDeviceById(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "id", id));
    }

    @Transactional
    public Device getDeviceByIdWithLock(UUID id) {
        return deviceRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device", "id", id));
    }

    @Transactional(readOnly = true)
    public Page<Device> searchDevices(String imei, String brand, String model, Pageable pageable) {
        return deviceRepository.searchDevices(imei, brand, model, pageable);
    }

    @Transactional
    public void updateDeviceStatus(UUID id, DeviceStatus status) {
        Device device = getDeviceById(id);
        
        if (device.getStatus() == status) {
            return;
        }
        
        device.setStatus(status);
        device.setUpdatedBy(currentUserService.getCurrentUser());
        
        device = deviceRepository.save(device);
        
        LifecycleEventType eventType = status == DeviceStatus.BLOCKED ? 
                LifecycleEventType.DEVICE_BLOCKED : LifecycleEventType.DEVICE_UNBLOCKED;
                
        lifecycleService.recordEvent(device, eventType, null, "Device status changed to " + status);
    }
    
    @Transactional
    public void recordImeiVerification(UUID id) {
        Device device = getDeviceById(id);
        
        // At this level we already validated on creation, but this is a manual trigger event
        String imei1 = imeiVerificationProvider.verifyImei(device.getImei1(), "IMEI1");
        
        lifecycleService.recordEvent(device, LifecycleEventType.IMEI_VERIFIED, null, "IMEI verification successful for " + imei1);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.buysell.modules.device.entity.DeviceLifecycleHistory> getDeviceLifecycleHistory(UUID id) {
        return lifecycleService.getHistory(id);
    }
}
