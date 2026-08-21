package com.buysell.modules.device.service;

import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.entity.DeviceCondition;
import com.buysell.modules.device.enums.LifecycleEventType;
import com.buysell.modules.device.repository.DeviceConditionRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceConditionService {

    private final DeviceConditionRepository conditionRepository;
    private final DeviceService deviceService;
    private final DeviceLifecycleService lifecycleService;
    private final CurrentUserService currentUserService;

    @Transactional
    public DeviceCondition recordCondition(UUID deviceId, DeviceCondition request) {
        Device device = deviceService.getDeviceById(deviceId);

        // Always create a new record (append-only history)
        DeviceCondition condition = DeviceCondition.builder()
                .device(device)
                .batteryHealth(request.getBatteryHealth())
                .displayCondition(request.getDisplayCondition())
                .bodyCondition(request.getBodyCondition())
                .cameraCondition(request.getCameraCondition())
                .speakerCondition(request.getSpeakerCondition())
                .microphoneCondition(request.getMicrophoneCondition())
                .chargingCondition(request.getChargingCondition())
                .biometricStatus(request.getBiometricStatus())
                .networkLock(request.getNetworkLock())
                .hasOriginalBill(request.getHasOriginalBill())
                .hasBox(request.getHasBox())
                .hasCharger(request.getHasCharger())
                .accessories(request.getAccessories())
                .notes(request.getNotes())
                .createdBy(currentUserService.getCurrentUser())
                .build();

        condition = conditionRepository.save(condition);

        lifecycleService.recordEvent(device, LifecycleEventType.CONDITION_RECORDED, condition.getId().toString(), "Device condition recorded");

        return condition;
    }

    @Transactional(readOnly = true)
    public List<DeviceCondition> getConditionHistory(UUID deviceId) {
        return conditionRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
    }

    @Transactional(readOnly = true)
    public DeviceCondition getLatestCondition(UUID deviceId) {
        return conditionRepository.findLatestByDeviceId(deviceId).orElse(null);
    }
}
