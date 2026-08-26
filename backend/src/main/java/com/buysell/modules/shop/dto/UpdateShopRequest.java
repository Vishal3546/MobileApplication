package com.buysell.modules.shop.dto;

import com.buysell.modules.shop.entity.ShopStatus;
import lombok.Data;

@Data
public class UpdateShopRequest {
    private String name;
    private String legalName;
    private String phone;
    private String email;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private ShopStatus status;
}
