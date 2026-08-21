package com.buysell.modules.device.entity;

import com.buysell.modules.device.enums.InspectionStatus;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_inspections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DeviceInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(name = "display_test", length = 20)
    private InspectionStatus displayTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "touch_test", length = 20)
    private InspectionStatus touchTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "camera_test", length = 20)
    private InspectionStatus cameraTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaker_test", length = 20)
    private InspectionStatus speakerTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "microphone_test", length = 20)
    private InspectionStatus microphoneTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "charging_test", length = 20)
    private InspectionStatus chargingTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "wifi_test", length = 20)
    private InspectionStatus wifiTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "bluetooth_test", length = 20)
    private InspectionStatus bluetoothTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "sim_test", length = 20)
    private InspectionStatus simTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "fingerprint_test", length = 20)
    private InspectionStatus fingerprintTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "face_id_test", length = 20)
    private InspectionStatus faceIdTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "battery_test", length = 20)
    private InspectionStatus batteryTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "flash_test", length = 20)
    private InspectionStatus flashTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "vibration_test", length = 20)
    private InspectionStatus vibrationTest;

    @Enumerated(EnumType.STRING)
    @Column(name = "network_test", length = 20)
    private InspectionStatus networkTest;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_status", nullable = false, length = 20)
    private InspectionStatus finalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspected_by")
    private User inspectedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
