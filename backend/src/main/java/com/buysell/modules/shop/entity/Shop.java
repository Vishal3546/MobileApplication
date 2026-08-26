package com.buysell.modules.shop.entity;

import com.buysell.common.entity.BaseEntity;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.user.entity.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shops")
public class Shop extends BaseEntity {

    @Column(name = "shop_code", nullable = false, unique = true, length = 50)
    private String shopCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "legal_name", length = 150)
    private String legalName;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Builder.Default
    private ShopStatus status = ShopStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
}
