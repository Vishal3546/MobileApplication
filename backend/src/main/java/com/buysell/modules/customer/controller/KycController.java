package com.buysell.modules.customer.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.customer.dto.KycDocumentResponse;
import com.buysell.modules.customer.dto.UploadKycRequest;
import com.buysell.modules.customer.service.KycService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}/documents")
@RequiredArgsConstructor
public class KycController {

    private final KycService kycService;

    @PostMapping
    @PreAuthorize("hasAuthority('UPLOAD_KYC')")
    public ResponseEntity<ApiResponse<KycDocumentResponse>> uploadKyc(
            @PathVariable UUID customerId,
            @Valid @RequestBody UploadKycRequest request) {
        return ResponseEntity.ok(ApiResponse.success(kycService.uploadKyc(customerId, request)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_KYC')")
    public ResponseEntity<ApiResponse<List<KycDocumentResponse>>> getCustomerDocuments(
            @PathVariable UUID customerId) {
        return ResponseEntity.ok(ApiResponse.success(kycService.getDocumentsByCustomer(customerId)));
    }

    @PostMapping("/{documentId}/approve")
    @PreAuthorize("hasAuthority('VERIFY_KYC')")
    public ResponseEntity<ApiResponse<KycDocumentResponse>> approveDocument(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success(kycService.approveDocument(customerId, documentId, notes)));
    }

    @PostMapping("/{documentId}/reject")
    @PreAuthorize("hasAuthority('REJECT_KYC')")
    public ResponseEntity<ApiResponse<KycDocumentResponse>> rejectDocument(
            @PathVariable UUID customerId,
            @PathVariable UUID documentId,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success(kycService.rejectDocument(customerId, documentId, notes)));
    }
}
