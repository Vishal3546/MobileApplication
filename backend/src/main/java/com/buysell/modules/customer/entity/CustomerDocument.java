package com.buysell.modules.customer.entity;

import com.buysell.common.entity.BaseEntity;
import com.buysell.modules.customer.enums.IdType;
import com.buysell.modules.customer.enums.VerificationStatus;
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
@Table(name = "customer_documents")
public class CustomerDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "id_type", nullable = false, length = 50)
    private IdType idType;

    @Column(name = "id_number_encrypted", nullable = false, columnDefinition = "TEXT")
    private String idNumberEncrypted;

    @Column(name = "id_number_hash", nullable = false, length = 64)
    private String idNumberHash;

    @Column(name = "id_number_masked", nullable = false, length = 50)
    private String idNumberMasked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "front_media_id")
    private MediaFile frontMedia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "back_media_id")
    private MediaFile backMedia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_media_id")
    private MediaFile photoMedia;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 20)
    @Builder.Default
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
}
