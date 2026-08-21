package com.buysell.modules.customer.entity;

import com.buysell.common.entity.BaseEntity;
import com.buysell.modules.customer.enums.ConsentType;
import com.buysell.modules.media.entity.MediaFile;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_consents")
public class CustomerConsent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false, length = 50)
    private ConsentType consentType;

    @Column(name = "consent_text_version", nullable = false, length = 50)
    private String consentTextVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "signature_media_id")
    private MediaFile signatureMedia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_media_id")
    private MediaFile videoMedia;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captured_by", nullable = false)
    private User capturedBy;

    @Column(name = "captured_at", nullable = false)
    @Builder.Default
    private LocalDateTime capturedAt = LocalDateTime.now();

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "reference_id")
    private java.util.UUID referenceId;
}
