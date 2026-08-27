package com.buysell.modules.settlement.entity;

import com.buysell.modules.settlement.enums.SettlementPaymentMode;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "settlement_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private ShopSettlement settlement;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementPaymentMode paymentMode;

    @Column
    private String referenceNumber;

    @Column(nullable = false)
    private String idempotencyKey;

    @Column(nullable = false)
    @Builder.Default
    private String status = "COMPLETED"; // Keeping simple string for now, could be enum

    @Column(nullable = false)
    @Builder.Default
    private ZonedDateTime paidAt = ZonedDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;
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
    public BigDecimal getAmount() {
        return this.amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public SettlementPaymentMode getPaymentMode() {
        return this.paymentMode;
    }
    public void setPaymentMode(SettlementPaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }
    public String getReferenceNumber() {
        return this.referenceNumber;
    }
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    public String getIdempotencyKey() {
        return this.idempotencyKey;
    }
    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public ZonedDateTime getPaidAt() {
        return this.paidAt;
    }
    public void setPaidAt(ZonedDateTime paidAt) {
        this.paidAt = paidAt;
    }
    public User getCreatedBy() {
        return this.createdBy;
    }
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
