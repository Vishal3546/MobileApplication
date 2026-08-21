package com.buysell.modules.device.service;

import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.entity.DeviceInspection;
import com.buysell.modules.device.enums.LifecycleEventType;
import com.buysell.modules.device.repository.DeviceInspectionRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceInspectionService {

    private final DeviceInspectionRepository inspectionRepository;
    private final DeviceService deviceService;
    private final DeviceLifecycleService lifecycleService;
    private final CurrentUserService currentUserService;

    @Transactional
    public DeviceInspection recordInspection(UUID deviceId, DeviceInspection request) {
        Device device = deviceService.getDeviceById(deviceId);

        // Always create a new record
        DeviceInspection inspection = DeviceInspection.builder()
                .device(device)
                .displayTest(request.getDisplayTest())
                .touchTest(request.getTouchTest())
                .cameraTest(request.getCameraTest())
                .speakerTest(request.getSpeakerTest())
                .microphoneTest(request.getMicrophoneTest())
                .chargingTest(request.getChargingTest())
                .wifiTest(request.getWifiTest())
                .bluetoothTest(request.getBluetoothTest())
                .simTest(request.getSimTest())
                .fingerprintTest(request.getFingerprintTest())
                .faceIdTest(request.getFaceIdTest())
                .batteryTest(request.getBatteryTest())
                .flashTest(request.getFlashTest())
                .vibrationTest(request.getVibrationTest())
                .networkTest(request.getNetworkTest())
                .notes(request.getNotes())
                .finalStatus(request.getFinalStatus())
                .inspectedBy(currentUserService.getCurrentUser())
                .build();

        inspection = inspectionRepository.save(inspection);

        lifecycleService.recordEvent(device, LifecycleEventType.INSPECTION_CREATED, inspection.getId().toString(), "Device inspection completed with status " + inspection.getFinalStatus());

        return inspection;
    }

    @Transactional(readOnly = true)
    public List<DeviceInspection> getInspectionHistory(UUID deviceId) {
        return inspectionRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
    }
}
