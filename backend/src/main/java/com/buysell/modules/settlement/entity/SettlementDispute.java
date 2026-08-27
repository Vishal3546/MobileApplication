package com.buysell.modules.settlement.entity;

import com.buysell.modules.settlement.enums.DisputeReason;
import com.buysell.modules.settlement.enums.DisputeStatus;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlement_disputes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementDispute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private ShopSettlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisputeReason reason;

    @Column(precision = 19, scale = 2)
    private BigDecimal claimedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DisputeStatus status = DisputeStatus.OPEN;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    @Column
    private ZonedDateTime resolvedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
    public UUID getId() {
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public ShopSettlement getSettlement() {
        return this.settlement;
    }
    public void setSettlement(ShopSettlement settlement) {
        this.settlement = settlement;
    }
    public User getRaisedBy() {
        return this.raisedBy;
    }
    public void setRaisedBy(User raisedBy) {
        this.raisedBy = raisedBy;
    }
    public DisputeReason getReason() {
        return this.reason;
    }
    public void setReason(DisputeReason reason) {
        this.reason = reason;
    }
    public BigDecimal getClaimedAmount() {
        return this.claimedAmount;
    }
    public void setClaimedAmount(BigDecimal claimedAmount) {
        this.claimedAmount = claimedAmount;
    }
    public DisputeStatus getStatus() {
        return this.status;
    }
    public void setStatus(DisputeStatus status) {
        this.status = status;
    }
    public String getResolution() {
        return this.resolution;
    }
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
    public User getResolvedBy() {
        return this.resolvedBy;
    }
    public void setResolvedBy(User resolvedBy) {
        this.resolvedBy = resolvedBy;
    }
    public ZonedDateTime getResolvedAt() {
        return this.resolvedAt;
    }
    public void setResolvedAt(ZonedDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public ZonedDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
