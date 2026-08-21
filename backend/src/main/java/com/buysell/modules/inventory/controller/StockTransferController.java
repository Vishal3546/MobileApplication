package com.buysell.modules.inventory.controller;

import com.buysell.modules.inventory.dto.StockTransferRequest;
import com.buysell.modules.inventory.dto.StockTransferResponse;
import com.buysell.modules.inventory.enums.TransferStatus;
import com.buysell.modules.inventory.service.StockTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/transfers")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService transferService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_STOCK_TRANSFER')")
    public ResponseEntity<StockTransferResponse> createTransfer(@Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.ok(transferService.createTransfer(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_STOCK_TRANSFER')")
    public ResponseEntity<StockTransferResponse> getTransferById(@PathVariable UUID id) {
        return ResponseEntity.ok(transferService.getTransferById(id));
    }

    @PostMapping("/{id}/transition")
    @PreAuthorize("hasAuthority('VIEW_STOCK_TRANSFER')") // Specific permissions are checked in service (like APPROVE_STOCK_TRANSFER)
    public ResponseEntity<StockTransferResponse> transitionTransfer(
            @PathVariable UUID id, 
            @RequestParam TransferStatus status) {
        return ResponseEntity.ok(transferService.transitionTransfer(id, status));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('COMPLETE_STOCK_TRANSFER')")
    public ResponseEntity<StockTransferResponse> completeTransfer(@PathVariable UUID id) {
        return ResponseEntity.ok(transferService.completeTransfer(id));
    }
}
