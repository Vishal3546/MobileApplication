package com.buysell.modules.branch.entity;

import com.buysell.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "branches")
public class Branch extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private com.buysell.modules.shop.entity.Shop shop;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "contact_number", length = 20)
    private String contactNumber;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
