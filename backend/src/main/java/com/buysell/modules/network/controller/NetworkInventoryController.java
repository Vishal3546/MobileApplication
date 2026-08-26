package com.buysell.modules.network.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.network.dto.NetworkInventoryResponse;
import com.buysell.modules.network.service.NetworkInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/network/inventory")
@RequiredArgsConstructor
public class NetworkInventoryController {

    private final NetworkInventoryService networkInventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NetworkInventoryResponse>>> getNetworkInventory(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String condition,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<NetworkInventoryResponse> response = networkInventoryService.getNetworkInventory(brand, model, condition, pageable);
        return ResponseEntity.ok(ApiResponse.success(response, "Network inventory retrieved successfully"));
    }
}
