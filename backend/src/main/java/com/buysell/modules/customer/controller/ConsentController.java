package com.buysell.modules.customer.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.customer.dto.CaptureConsentRequest;
import com.buysell.modules.customer.dto.ConsentResponse;
import com.buysell.modules.customer.service.ConsentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @PostMapping
    @PreAuthorize("hasAuthority('VIEW_CUSTOMER_CONSENT')") // In practice, capturing consent might require a specific role, or anyone who can interact with a customer.
    public ResponseEntity<ApiResponse<ConsentResponse>> captureConsent(
            @PathVariable UUID customerId,
            @Valid @RequestBody CaptureConsentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(consentService.captureConsent(customerId, request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CUSTOMER_CONSENT')")
    public ResponseEntity<ApiResponse<List<ConsentResponse>>> getCustomerConsents(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(consentService.getConsentsByCustomer(customerId)));
    }
}
