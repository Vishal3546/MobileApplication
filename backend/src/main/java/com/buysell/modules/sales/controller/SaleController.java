package com.buysell.modules.sales.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.sales.dto.CreateSalePaymentRequest;
import com.buysell.modules.sales.dto.CreateSaleRequest;
import com.buysell.modules.sales.dto.OverrideSalePriceRequest;
import com.buysell.modules.sales.dto.SalePaymentResponse;
import com.buysell.modules.sales.dto.SaleTransactionResponse;
import com.buysell.modules.sales.service.SalePaymentService;
import com.buysell.modules.sales.service.SaleService;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final SaleService saleService;
    private final SalePaymentService salePaymentService;

    public SaleController(SaleService saleService, SalePaymentService salePaymentService) {
        this.saleService = saleService;
        this.salePaymentService = salePaymentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SaleTransactionResponse>> createSale(@Valid @RequestBody CreateSaleRequest request) {
        SaleTransactionResponse response = saleService.createSale(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Sale created and inventory reserved successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/override-price")
    public ResponseEntity<ApiResponse<SaleTransactionResponse>> overridePrice(
            @PathVariable UUID id, @Valid @RequestBody OverrideSalePriceRequest request) {
        SaleTransactionResponse response = saleService.overridePrice(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Sale price overridden successfully"));
    }

    @PostMapping("/payments")
    public ResponseEntity<ApiResponse<SalePaymentResponse>> createPayment(@Valid @RequestBody CreateSalePaymentRequest request) {
        SalePaymentResponse response = salePaymentService.createPayment(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Payment created successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<SaleTransactionResponse>> completeSale(@PathVariable UUID id) {
        SaleTransactionResponse response = saleService.completeSale(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Sale completed and invoice generated successfully"));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<SaleTransactionResponse>> cancelSale(@PathVariable UUID id, @RequestBody String reason) {
        SaleTransactionResponse response = saleService.cancelSale(id, reason);
        return ResponseEntity.ok(ApiResponse.success(response, "Sale cancelled successfully"));
    }
}
