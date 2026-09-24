package com.buysell.modules.purchase.controller;

import com.buysell.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import com.buysell.modules.purchase.dto.CreatePurchasePaymentRequest;
import com.buysell.modules.purchase.dto.CreatePurchaseRequest;
import com.buysell.modules.purchase.dto.PurchasePaymentResponse;
import com.buysell.modules.purchase.dto.PurchaseResponse;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.mapper.PurchaseMapper;
import com.buysell.modules.purchase.service.PurchasePaymentService;
import com.buysell.modules.purchase.service.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final PurchasePaymentService paymentService;
    private final PurchaseMapper purchaseMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PURCHASE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseResponse createPurchase(@Valid @RequestBody CreatePurchaseRequest request) {
        return purchaseMapper.toResponse(purchaseService.createPurchase(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PURCHASES')")
    @Operation(summary = "List purchases (paginated, app purchase list screen)")
    public org.springframework.data.domain.Page<PurchaseResponse> getPurchases(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @org.springframework.data.web.PageableDefault(size = 20, sort = "createdAt",
                    direction = org.springframework.data.domain.Sort.Direction.DESC)
            org.springframework.data.domain.Pageable pageable) {
        TransactionStatus statusEnum = null;
        if (status != null && !status.trim().isEmpty()) {
            try {
                statusEnum = TransactionStatus.valueOf(status.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("INVALID_STATUS", "Unknown purchase status: " + status, HttpStatus.BAD_REQUEST);
            }
        }
        java.time.LocalDateTime start = parseDate(startDate, "startDate");
        java.time.LocalDateTime end = parseDate(endDate, "endDate");
        return purchaseService.getPurchases(search, statusEnum, start, end, pageable)
                .map(purchaseMapper::toResponse);
    }

    private java.time.LocalDateTime parseDate(String value, String field) {
        if (value == null || value.trim().isEmpty()) return null;
        String v = value.trim();
        try {
            return java.time.LocalDateTime.parse(v); // full ISO date-time
        } catch (Exception ignored) {
            try {
                // date-only: start -> start of day, end -> start of next day (exclusive)
                java.time.LocalDate d = java.time.LocalDate.parse(v);
                return "endDate".equals(field) ? d.plusDays(1).atStartOfDay() : d.atStartOfDay();
            } catch (Exception e) {
                throw new BusinessException("INVALID_DATE", field + " must be an ISO date or date-time: " + value, HttpStatus.BAD_REQUEST);
            }
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PURCHASES')")
    public PurchaseResponse getPurchase(@PathVariable UUID id) {
        return purchaseMapper.toResponse(purchaseService.getAndValidateAccess(id));
    }

    @PostMapping("/{id}/transition")
    @PreAuthorize("hasAuthority('TRANSITION_PURCHASE')")
    public PurchaseResponse transitionStatus(@PathVariable UUID id, @RequestParam TransactionStatus status, @RequestParam(required = false) String reason) {
        return purchaseMapper.toResponse(purchaseService.transitionStatus(id, status, reason));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAuthority('CANCEL_PURCHASE')")
    public PurchaseResponse cancelPurchase(@PathVariable UUID id, @RequestParam(required = false) String reason) {
        return purchaseMapper.toResponse(purchaseService.transitionStatus(id, TransactionStatus.CANCELLED, reason != null ? reason : "Cancelled via API"));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('CREATE_PURCHASE_PAYMENT')")
    @ResponseStatus(HttpStatus.CREATED)
    public PurchasePaymentResponse createPayment(@PathVariable UUID id, @Valid @RequestBody CreatePurchasePaymentRequest request) {
        return purchaseMapper.toPaymentResponse(paymentService.processPayment(purchaseService.getAndValidateAccess(id), request));
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('VIEW_PURCHASE_PAYMENTS')")
    public List<PurchasePaymentResponse> getPayments(@PathVariable UUID id) {
        // Validate access first
        purchaseService.getAndValidateAccess(id);
        return paymentService.getPaymentsForPurchase(id).stream()
                .map(purchaseMapper::toPaymentResponse)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAuthority('COMPLETE_PURCHASE')")
    public PurchaseResponse completePurchase(@PathVariable UUID id) {
        purchaseService.completePurchase(id);
        return purchaseMapper.toResponse(purchaseService.getAndValidateAccess(id));
    }
}
