package com.buysell.modules.shop.dto;

import com.buysell.modules.shop.entity.ShopStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ShopResponse {
    private UUID id;
    private String shopCode;
    private String name;
    private String legalName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private ShopStatus status;
    private UUID ownerUserId;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
