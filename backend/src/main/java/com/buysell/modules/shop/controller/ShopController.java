package com.buysell.modules.shop.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.shop.dto.CreateShopRequest;
import com.buysell.modules.shop.dto.ShopResponse;
import com.buysell.modules.shop.dto.UpdateShopRequest;
import com.buysell.modules.shop.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShopResponse>> createShop(@Valid @RequestBody CreateShopRequest request) {
        ShopResponse response = shopService.createShop(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Shop created successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getAllShops() {
        List<ShopResponse> response = shopService.getAllShops();
        return ResponseEntity.ok(ApiResponse.success(response, "Shops retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopById(@PathVariable UUID id) {
        ShopResponse response = shopService.getShopById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Shop retrieved successfully"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShop(@PathVariable UUID id, @Valid @RequestBody UpdateShopRequest request) {
        ShopResponse response = shopService.updateShop(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Shop updated successfully"));
    }
}
