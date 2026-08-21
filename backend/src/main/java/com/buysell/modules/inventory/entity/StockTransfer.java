package com.buysell.modules.inventory.entity;

import com.buysell.common.entity.BaseEntity;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.inventory.enums.TransferStatus;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stock_transfers")
public class StockTransfer extends BaseEntity {

    @Column(name = "transfer_number", nullable = false, unique = true)
    private String transferNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_branch_id", nullable = false)
    private Branch fromBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_branch_id", nullable = false)
    private Branch toBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransferStatus status = TransferStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by")
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "requested_at")
    private ZonedDateTime requestedAt;

    @Column(name = "approved_at")
    private ZonedDateTime approvedAt;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @OneToMany(mappedBy = "stockTransfer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockTransferItem> items = new ArrayList<>();

    public void addItem(StockTransferItem item) {
        items.add(item);
        item.setStockTransfer(this);
    }

    public void removeItem(StockTransferItem item) {
        items.remove(item);
        item.setStockTransfer(null);
    }
}
