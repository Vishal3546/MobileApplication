package com.buysell.modules.settlement.entity;

import com.buysell.modules.inventory.entity.StockTransfer;
import com.buysell.modules.settlement.enums.SettlementStatus;
import com.buysell.modules.shop.entity.Shop;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "shop_settlements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String settlementNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_shop_id", nullable = false)
    private Shop sourceShop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_shop_id", nullable = false)
    private Shop destinationShop;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transfer_id", nullable = false, unique = true)
    private StockTransfer transfer;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal grossAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal remainingAmount;

    @Column(nullable = false)
    @Builder.Default
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SettlementStatus status = SettlementStatus.PENDING;

    @Column
    private ZonedDateTime dueAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SettlementPayment> payments = new ArrayList<>();

    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SettlementDispute> disputes = new ArrayList<>();
    public UUID getId() {
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getSettlementNumber() {
        return this.settlementNumber;
    }
    public void setSettlementNumber(String settlementNumber) {
        this.settlementNumber = settlementNumber;
    }
    public Shop getSourceShop() {
        return this.sourceShop;
    }
    public void setSourceShop(Shop sourceShop) {
        this.sourceShop = sourceShop;
    }
    public Shop getDestinationShop() {
        return this.destinationShop;
    }
    public void setDestinationShop(Shop destinationShop) {
        this.destinationShop = destinationShop;
    }
    public StockTransfer getTransfer() {
        return this.transfer;
    }
    public void setTransfer(StockTransfer transfer) {
        this.transfer = transfer;
    }
    public BigDecimal getGrossAmount() {
        return this.grossAmount;
    }
    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }
    public BigDecimal getPaidAmount() {
        return this.paidAmount;
    }
    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
    public BigDecimal getRemainingAmount() {
        return this.remainingAmount;
    }
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
    public String getCurrency() {
        return this.currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public SettlementStatus getStatus() {
        return this.status;
    }
    public void setStatus(SettlementStatus status) {
        this.status = status;
    }
    public ZonedDateTime getDueAt() {
        return this.dueAt;
    }
    public void setDueAt(ZonedDateTime dueAt) {
        this.dueAt = dueAt;
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
    public List<SettlementPayment> getPayments() {
        return this.payments;
    }
    public void setPayments(List<SettlementPayment> payments) {
        this.payments = payments;
    }
    public List<SettlementDispute> getDisputes() {
        return this.disputes;
    }
    public void setDisputes(List<SettlementDispute> disputes) {
        this.disputes = disputes;
    }
}
