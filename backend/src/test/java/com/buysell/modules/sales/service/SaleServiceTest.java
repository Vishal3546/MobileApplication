package com.buysell.modules.sales.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.inventory.service.InventoryService;
import com.buysell.modules.sales.dto.CreateSaleRequest;
import com.buysell.modules.sales.dto.SaleTransactionResponse;
import com.buysell.modules.sales.entity.SaleTransaction;
import com.buysell.modules.sales.repository.SaleStatusHistoryRepository;
import com.buysell.modules.sales.repository.SaleTransactionRepository;
import com.buysell.modules.sales.repository.SalesInvoiceRepository;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest {

    @Mock private SaleTransactionRepository saleTransactionRepository;
    @Mock private SaleStatusHistoryRepository saleStatusHistoryRepository;
    @Mock private SalesInvoiceRepository salesInvoiceRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private InventoryItemRepository inventoryItemRepository;
    @Mock private InventoryService inventoryService;
    @Mock private SalePricingService salePricingService;
    @Mock private WarrantyPolicyService warrantyPolicyService;
    @Mock private CurrentUserService currentUserService;
    @Mock private AuditService auditService;

    @InjectMocks
    private SaleService saleService;

    private User user;
    private Branch branch;
    private Customer customer;
    private InventoryItem inventory;

    @BeforeEach
    public void setUp() {
        assertNotNull(saleStatusHistoryRepository);
        assertNotNull(salesInvoiceRepository);
        assertNotNull(inventoryService);
        assertNotNull(warrantyPolicyService);
        assertNotNull(auditService);
        branch = Branch.builder().id(UUID.randomUUID()).build();
        user = User.builder().id(UUID.randomUUID()).build();
        customer = Customer.builder().id(UUID.randomUUID()).branch(branch).status(CustomerStatus.ACTIVE).build();
        inventory = InventoryItem.builder().branch(branch).status(InventoryStatus.AVAILABLE).build();
        inventory.setId(UUID.randomUUID());
    }

    @Test
    void createSale_Success() {
        CreateSaleRequest req = new CreateSaleRequest();
        req.setCustomerId(customer.getId());
        req.setInventoryItemId(inventory.getId());
        req.setSellingPrice(new BigDecimal("100"));
        req.setDiscountAmount(BigDecimal.ZERO);
        req.setTaxAmount(BigDecimal.ZERO);

        when(currentUserService.hasPermission("CREATE_SALE")).thenReturn(true);
        when(currentUserService.hasAccessToBranch(any())).thenReturn(true);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(inventoryItemRepository.findByIdWithLock(inventory.getId())).thenReturn(Optional.of(inventory));
        when(salePricingService.calculateFinalAmount(any(), any(), any())).thenReturn(new BigDecimal("100"));
        when(saleTransactionRepository.generateSaleNumber()).thenReturn("SALE-001");
        
        SaleTransaction saved = SaleTransaction.builder().id(UUID.randomUUID()).saleNumber("SALE-001")
            .customer(customer).inventoryItem(inventory).employee(user).branch(branch).build();
        when(saleTransactionRepository.save(any())).thenReturn(saved);

        SaleTransactionResponse res = saleService.createSale(req);
        assertNotNull(res);
        assertEquals("SALE-001", res.getSaleNumber());
    }
}
