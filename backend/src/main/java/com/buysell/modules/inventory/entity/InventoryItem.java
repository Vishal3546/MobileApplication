package com.buysell.modules.inventory.entity;

import com.buysell.common.entity.BaseEntity;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventory_items")
public class InventoryItem extends BaseEntity {

    @Column(name = "stock_code", nullable = false, unique = true)
    private String stockCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_transaction_id", nullable = false, unique = true)
    private PurchaseTransaction purchaseTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.AVAILABLE;

    @Column(name = "cost_price", nullable = false)
    private BigDecimal costPrice;

    @Column(name = "selling_price")
    private BigDecimal sellingPrice;

    @Column(name = "reserved_until")
    private ZonedDateTime reservedUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserved_by")
    private User reservedBy;

    @Column(name = "condition_summary")
    private String conditionSummary;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
