package com.buysell.modules.sales;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import com.buysell.exception.BusinessException;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.repository.BranchRepository;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.repository.DeviceRepository;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.repository.PurchaseTransactionRepository;
import com.buysell.modules.sales.dto.CreateSalePaymentRequest;
import com.buysell.modules.sales.dto.CreateSaleRequest;
import com.buysell.modules.sales.dto.OverrideSalePriceRequest;
import com.buysell.modules.sales.dto.SaleTransactionResponse;
import com.buysell.modules.sales.enums.PaymentMode;
import com.buysell.modules.sales.enums.SaleStatus;
import com.buysell.modules.sales.service.SalePaymentService;
import com.buysell.modules.sales.service.SaleService;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.UserRepository;
import com.buysell.security.CurrentUserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@Transactional
public class SaleIntegrationTest {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SalePaymentService salePaymentService;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PurchaseTransactionRepository purchaseTransactionRepository;

    @MockBean
    private CurrentUserService currentUserService;

    private User admin;
    private Branch branch;
    private Customer customer;
    private InventoryItem inventory;

    @BeforeEach
    public void setUp() {
        branch = branchRepository.save(Branch.builder().name("Branch 1 " + UUID.randomUUID()).isActive(true).build());
        admin = userRepository.save(User.builder().username("admin_" + UUID.randomUUID()).passwordHash("hash").isActive(true).build());
        customer = customerRepository.save(Customer.builder().firstName("Test").lastName("Customer").phone("123456" + UUID.randomUUID().toString().substring(0,5)).branch(branch).status(CustomerStatus.ACTIVE).build());

        Device device = deviceRepository.save(Device.builder().imei1("123" + UUID.randomUUID().toString().substring(0,10)).brand("Apple").model("iPhone 13").build());
        PurchaseTransaction purchase = purchaseTransactionRepository.save(PurchaseTransaction.builder()
                .purchaseNumber("PUR-" + UUID.randomUUID().toString().substring(0,8))
                .device(device).branch(branch).employee(admin).customer(customer).finalPrice(new BigDecimal("100.00")).transactionStatus(TransactionStatus.COMPLETED).build());

        inventory = inventoryItemRepository.save(InventoryItem.builder()
                .stockCode("STK-" + UUID.randomUUID().toString().substring(0,8))
                .branch(branch).device(device).purchaseTransaction(purchase).costPrice(new BigDecimal("100.00")).sellingPrice(new BigDecimal("150.00")).status(InventoryStatus.AVAILABLE).build());

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(currentUserService.getCurrentUserId()).thenReturn(admin.getId());
        when(currentUserService.getCurrentBranch()).thenReturn(branch);
        when(currentUserService.hasPermission(any())).thenReturn(true);
        when(currentUserService.isSuperAdmin()).thenReturn(true);
    }

    @Test
    void testCompleteSaleLifecycle() {
        // 1. Create Sale
        CreateSaleRequest saleReq = new CreateSaleRequest();
        saleReq.setCustomerId(customer.getId());
        saleReq.setInventoryItemId(inventory.getId());
        saleReq.setSellingPrice(new BigDecimal("150.00"));
        saleReq.setDiscountAmount(BigDecimal.ZERO);
        saleReq.setTaxAmount(BigDecimal.ZERO);

        SaleTransactionResponse sale = saleService.createSale(saleReq);
        assertNotNull(sale.getId());
        assertEquals(SaleStatus.RESERVED, sale.getSaleStatus());

        // Verify inventory is reserved
        InventoryItem item = inventoryItemRepository.findById(inventory.getId()).get();
        assertEquals(InventoryStatus.RESERVED, item.getStatus());

        // 2. Override Price
        OverrideSalePriceRequest overrideReq = new OverrideSalePriceRequest();
        overrideReq.setSellingPrice(new BigDecimal("160.00"));
        sale = saleService.overridePrice(sale.getId(), overrideReq);
        assertEquals(new BigDecimal("160.00"), sale.getFinalAmount());

        // 3. Make Partial Payment (Throws if we want full payment, but let's test amount logic)
        CreateSalePaymentRequest payReq1 = new CreateSalePaymentRequest();
        payReq1.setSaleTransactionId(sale.getId());
        payReq1.setPaymentMode(PaymentMode.CASH);
        payReq1.setAmount(new BigDecimal("100.00"));
        payReq1.setIdempotencyKey(UUID.randomUUID().toString());
        salePaymentService.createPayment(payReq1);

        // 4. Complete Sale (Should fail, not fully paid)
        final UUID saleId = sale.getId();
        BusinessException ex = assertThrows(BusinessException.class, () -> saleService.completeSale(saleId));
        assertEquals("SALE_PAYMENT_REQUIRED", ex.getCode());

        // 5. Make Remaining Payment
        CreateSalePaymentRequest payReq2 = new CreateSalePaymentRequest();
        payReq2.setSaleTransactionId(sale.getId());
        payReq2.setPaymentMode(PaymentMode.CARD);
        payReq2.setAmount(new BigDecimal("60.00"));
        payReq2.setIdempotencyKey(UUID.randomUUID().toString());
        salePaymentService.createPayment(payReq2);

        // 6. Complete Sale (Success)
        sale = saleService.completeSale(sale.getId());
        assertEquals(SaleStatus.COMPLETED, sale.getSaleStatus());

        // Verify inventory is sold
        item = inventoryItemRepository.findById(inventory.getId()).get();
        assertEquals(InventoryStatus.SOLD, item.getStatus());
    }

    @Test
    void testInventoryEligibility() {
        inventory.setStatus(InventoryStatus.SOLD);
        inventoryItemRepository.save(inventory);

        CreateSaleRequest saleReq = new CreateSaleRequest();
        saleReq.setCustomerId(customer.getId());
        saleReq.setInventoryItemId(inventory.getId());
        saleReq.setSellingPrice(new BigDecimal("150.00"));
        saleReq.setDiscountAmount(BigDecimal.ZERO);
        saleReq.setTaxAmount(BigDecimal.ZERO);

        BusinessException ex = assertThrows(BusinessException.class, () -> saleService.createSale(saleReq));
        assertEquals("INVENTORY_NOT_AVAILABLE", ex.getCode());
    }

    @Test
    void testDiscountAndPriceSecurity() {
        CreateSaleRequest saleReq = new CreateSaleRequest();
        saleReq.setCustomerId(customer.getId());
        saleReq.setInventoryItemId(inventory.getId());
        saleReq.setSellingPrice(new BigDecimal("150.00"));
        saleReq.setDiscountAmount(new BigDecimal("200.00")); // More than selling price
        saleReq.setTaxAmount(BigDecimal.ZERO);

        BusinessException ex = assertThrows(BusinessException.class, () -> saleService.createSale(saleReq));
        assertEquals("SALE_DISCOUNT_LIMIT_EXCEEDED", ex.getCode());
    }

    @Test
    void testBranchIsolation() {
        Branch otherBranch = branchRepository.save(Branch.builder().name("Other Branch " + UUID.randomUUID()).isActive(true).build());
        when(currentUserService.getCurrentBranch()).thenReturn(otherBranch);
        when(currentUserService.isSuperAdmin()).thenReturn(false); // Simulate non-admin

        CreateSaleRequest saleReq = new CreateSaleRequest();
        saleReq.setCustomerId(customer.getId()); // customer is in branch 1
        saleReq.setInventoryItemId(inventory.getId());
        saleReq.setSellingPrice(new BigDecimal("150.00"));
        saleReq.setDiscountAmount(BigDecimal.ZERO);
        saleReq.setTaxAmount(BigDecimal.ZERO);

        BusinessException ex = assertThrows(BusinessException.class, () -> saleService.createSale(saleReq));
        assertEquals("CUSTOMER_ACCESS_DENIED", ex.getCode());
    }
}
