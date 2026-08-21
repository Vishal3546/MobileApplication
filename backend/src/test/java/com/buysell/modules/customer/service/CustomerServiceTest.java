package com.buysell.modules.customer.service;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.service.BranchAccessService;
import com.buysell.modules.customer.dto.CustomerResponse;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BranchAccessService branchAccessService;

    @Mock
    private CurrentUserService currentUserService;
    
    @Mock
    private AuditService auditService;

    @InjectMocks
    private CustomerService customerService;

    private UUID customerId;
    private Customer customer;
    private Branch branch;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customerId = UUID.randomUUID();
        branch = Branch.builder().id(UUID.randomUUID()).build();
        customer = Customer.builder().id(customerId).branch(branch).build();
    }

    @Test
    void testGetCustomerSuccess() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(branchAccessService.canAccessBranch(branch.getId())).thenReturn(true);

        CustomerResponse response = customerService.getCustomerById(customerId);
        assertEquals(customerId, response.getId());
    }

    @Test
    void testGetCustomerUnauthorizedBranch() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(branchAccessService.canAccessBranch(branch.getId())).thenReturn(false); // Different branch

        assertThrows(AccessDeniedException.class, () -> {
            customerService.getCustomerById(customerId);
        });
    }
}
