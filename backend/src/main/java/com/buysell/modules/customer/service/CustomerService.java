package com.buysell.modules.customer.service;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.service.BranchAccessService;
import com.buysell.modules.customer.dto.CreateCustomerRequest;
import com.buysell.modules.customer.dto.CustomerResponse;
import com.buysell.modules.customer.dto.UpdateCustomerRequest;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BranchAccessService branchAccessService;
    private final CurrentUserService currentUserService;
    private final AuditService auditService; // Stub/Interface for Phase 2 audit

    @Transactional(readOnly = true)
    public Page<CustomerResponse> searchCustomers(String search, Pageable pageable) {
        UUID branchId = branchAccessService.isGlobalUser() ? null : branchAccessService.getCurrentBranchId();
        
        return customerRepository.searchCustomers(branchId, search, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = getAndValidateAccess(id);
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        Branch currentBranch = currentUserService.getCurrentBranch();
        
        Customer customer = Customer.builder()
                .branch(currentBranch)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .altPhone(request.getAltPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();
                
        customer = customerRepository.save(customer);
        
        UUID branchId = branchAccessService.isGlobalUser() ? null : branchAccessService.getCurrentBranchId();
        auditService.logAction(currentUserService.getCurrentUserId(), branchId, "CUSTOMER_CREATED", "Customer", customer.getId(), null, null, null, null);
        
        return mapToResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id, UpdateCustomerRequest request) {
        Customer customer = getAndValidateAccess(id);
        
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setAltPhone(request.getAltPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        
        if (request.getStatus() != null && customer.getStatus() != request.getStatus()) {
            if (!currentUserService.hasPermission("MANAGE_CUSTOMER_STATUS") && !currentUserService.isSuperAdmin()) {
                throw new AccessDeniedException("Not authorized to manage customer status");
            }
            customer.setStatus(request.getStatus());
            UUID branchId = branchAccessService.isGlobalUser() ? null : branchAccessService.getCurrentBranchId();
            auditService.logAction(currentUserService.getCurrentUserId(), branchId, "CUSTOMER_STATUS_CHANGED", "Customer", customer.getId(), null, request.getStatus().name(), null, null);
        }

        customer = customerRepository.save(customer);
        
        UUID branchId = branchAccessService.isGlobalUser() ? null : branchAccessService.getCurrentBranchId();
        auditService.logAction(currentUserService.getCurrentUserId(), branchId, "CUSTOMER_UPDATED", "Customer", customer.getId(), null, null, null, null);
        
        return mapToResponse(customer);
    }

    public Customer getAndValidateAccess(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
                
        if (!branchAccessService.canAccessBranch(customer.getBranch().getId())) {
            throw new AccessDeniedException("Not authorized to access this customer");
        }
        
        return customer;
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phone(customer.getPhone())
                .altPhone(customer.getAltPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .status(customer.getStatus())
                .verificationStatus(customer.getVerificationStatus())
                .build();
    }
}
