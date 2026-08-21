package com.buysell.modules.customer.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.customer.dto.CreateCustomerRequest;
import com.buysell.modules.customer.dto.CustomerResponse;
import com.buysell.modules.customer.dto.UpdateCustomerRequest;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<ApiResponse<Page<CustomerResponse>>> getCustomers(
            @RequestParam(required = false) String search,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(customerService.searchCustomers(search, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_CUSTOMERS')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomerById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(customerService.createCustomer(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable UUID id, 
            @Valid @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.success(customerService.updateCustomer(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('MANAGE_CUSTOMER_STATUS')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerStatus(
            @PathVariable UUID id, 
            @RequestParam CustomerStatus status) {
        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setStatus(status);
        // We fetch existing data to preserve it, though usually a dedicated status update DTO is cleaner.
        // For brevity in foundation, we re-use update logic which checks MANAGE_CUSTOMER_STATUS.
        CustomerResponse existing = customerService.getCustomerById(id);
        request.setFirstName(existing.getFirstName());
        request.setLastName(existing.getLastName());
        request.setPhone(existing.getPhone());
        request.setAltPhone(existing.getAltPhone());
        request.setEmail(existing.getEmail());
        request.setAddress(existing.getAddress());
        
        return ResponseEntity.ok(ApiResponse.success(customerService.updateCustomer(id, request)));
    }
}
