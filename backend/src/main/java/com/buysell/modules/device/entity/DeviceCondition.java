package com.buysell.modules.device.entity;

import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "device_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DeviceCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(name = "battery_health")
    private Integer batteryHealth; // 0-100

    @Column(name = "display_condition", length = 50)
    private String displayCondition;

    @Column(name = "body_condition", length = 50)
    private String bodyCondition;

    @Column(name = "camera_condition", length = 50)
    private String cameraCondition;

    @Column(name = "speaker_condition", length = 50)
    private String speakerCondition;

    @Column(name = "microphone_condition", length = 50)
    private String microphoneCondition;

    @Column(name = "charging_condition", length = 50)
    private String chargingCondition;

    @Column(name = "biometric_status", length = 50)
    private String biometricStatus;

    @Column(name = "network_lock", length = 50)
    private String networkLock;

    @Column(name = "has_original_bill")
    @Builder.Default
    private Boolean hasOriginalBill = false;

    @Column(name = "has_box")
    @Builder.Default
    private Boolean hasBox = false;

    @Column(name = "has_charger")
    @Builder.Default
    private Boolean hasCharger = false;

    @Column(name = "accessories", columnDefinition = "TEXT")
    private String accessories;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
