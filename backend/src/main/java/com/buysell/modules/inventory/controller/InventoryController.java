package com.buysell.modules.inventory.controller;

import com.buysell.modules.inventory.dto.*;
import com.buysell.modules.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_INVENTORY')")
    public ResponseEntity<InventoryResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/stock/{stockCode}")
    @PreAuthorize("hasAuthority('VIEW_INVENTORY')")
    public ResponseEntity<InventoryResponse> getByStockCode(@PathVariable String stockCode) {
        return ResponseEntity.ok(inventoryService.getInventoryByStockCode(stockCode));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('CHANGE_INVENTORY_STATUS')")
    public ResponseEntity<InventoryResponse> changeStatus(
            @PathVariable UUID id, 
            @Valid @RequestBody ChangeInventoryStatusRequest request) {
        return ResponseEntity.ok(inventoryService.changeStatus(id, request));
    }

    @PatchMapping("/{id}/selling-price")
    @PreAuthorize("hasAuthority('UPDATE_SELLING_PRICE')")
    public ResponseEntity<InventoryResponse> updateSellingPrice(
            @PathVariable UUID id, 
            @Valid @RequestBody UpdateSellingPriceRequest request) {
        return ResponseEntity.ok(inventoryService.updateSellingPrice(id, request));
    }

    @PostMapping("/{id}/reserve")
    @PreAuthorize("hasAuthority('RESERVE_INVENTORY')")
    public ResponseEntity<InventoryResponse> reserveInventory(
            @PathVariable UUID id, 
            @Valid @RequestBody ReserveInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.reserveInventory(id, request));
    }

    @PostMapping("/{id}/release")
    @PreAuthorize("hasAuthority('RELEASE_INVENTORY')")
    public ResponseEntity<InventoryResponse> releaseInventory(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.releaseInventory(id));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAuthority('VIEW_INVENTORY_HISTORY')")
    public ResponseEntity<List<InventoryStatusHistoryResponse>> getHistory(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.getInventoryHistory(id));
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('VIEW_INVENTORY_SUMMARY')")
    public ResponseEntity<InventorySummaryResponse> getSummary(
            @RequestParam(required = false) UUID branchId) {
        return ResponseEntity.ok(inventoryService.getInventorySummary(branchId));
    }
}
