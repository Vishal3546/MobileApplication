package com.buysell.modules.shop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateShopRequest {
    @NotBlank(message = "Shop name is required")
    private String name;

    private String legalName;
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String address;
    private String city;
    private String state;
    private String postalCode;

    // Optional: If provided, assigns this user as the OWNER of the new shop.
    private UUID ownerUserId;
}
