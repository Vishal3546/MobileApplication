package com.buysell.modules.purchase.entity;

import com.buysell.common.entity.BaseEntity;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "purchase_transactions")
public class PurchaseTransaction extends BaseEntity {

    @Column(name = "purchase_number", nullable = false, unique = true)
    private String purchaseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "suggested_price")
    private BigDecimal suggestedPrice;

    @Column(name = "negotiated_price")
    private BigDecimal negotiatedPrice;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "notes")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    @Builder.Default
    private TransactionStatus transactionStatus = TransactionStatus.INITIATED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
}
