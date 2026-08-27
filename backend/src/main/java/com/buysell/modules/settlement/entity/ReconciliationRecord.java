package com.buysell.modules.settlement.entity;

import com.buysell.modules.settlement.enums.ReconciliationStatus;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "reconciliation_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private ShopSettlement settlement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal expectedAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal actualAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal difference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReconciliationStatus status = ReconciliationStatus.PENDING_REVIEW;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reconciled_by_id")
    private User reconciledBy;

    @Column
    private ZonedDateTime reconciledAt;

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
    public Shop getShop() {
        return this.shop;
    }
    public void setShop(Shop shop) {
        this.shop = shop;
    }
    public BigDecimal getExpectedAmount() {
        return this.expectedAmount;
    }
    public void setExpectedAmount(BigDecimal expectedAmount) {
        this.expectedAmount = expectedAmount;
    }
    public BigDecimal getActualAmount() {
        return this.actualAmount;
    }
    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }
    public BigDecimal getDifference() {
        return this.difference;
    }
    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }
    public ReconciliationStatus getStatus() {
        return this.status;
    }
    public void setStatus(ReconciliationStatus status) {
        this.status = status;
    }
    public String getNotes() {
        return this.notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public User getReconciledBy() {
        return this.reconciledBy;
    }
    public void setReconciledBy(User reconciledBy) {
        this.reconciledBy = reconciledBy;
    }
    public ZonedDateTime getReconciledAt() {
        return this.reconciledAt;
    }
    public void setReconciledAt(ZonedDateTime reconciledAt) {
        this.reconciledAt = reconciledAt;
    }
    public ZonedDateTime getCreatedAt() {
        return this.createdAt;
    }
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
