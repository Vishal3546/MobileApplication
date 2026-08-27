package com.buysell.modules.inventory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.repository.DeviceRepository;
import com.buysell.modules.inventory.dto.ChangeInventoryStatusRequest;
import com.buysell.modules.inventory.dto.InventoryResponse;
import com.buysell.modules.inventory.dto.ReserveInventoryRequest;
import com.buysell.modules.inventory.dto.StockTransferRequest;
import com.buysell.modules.inventory.dto.StockTransferResponse;
import com.buysell.modules.inventory.dto.UpdateSellingPriceRequest;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.enums.TransferStatus;
import com.buysell.modules.inventory.service.InventoryCreationService;
import com.buysell.modules.inventory.service.InventoryService;
import com.buysell.modules.inventory.service.StockTransferService;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.repository.PurchaseTransactionRepository;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.UserRepository;
import com.buysell.security.CurrentUserService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@Transactional
public class InventoryIntegrationTest {

    @Autowired
    private InventoryCreationService inventoryCreationService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockTransferService stockTransferService;


    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PurchaseTransactionRepository purchaseTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @MockBean
    private CurrentUserService currentUserService;


    @Autowired
    private com.buysell.modules.shop.repository.ShopRepository shopRepository;

    private Branch branch1;
    private Branch branch2;
    private User admin;
    private Device device;
    private PurchaseTransaction purchase;
    private com.buysell.modules.shop.entity.Shop mainShop;

    @BeforeEach
    public void setUp() {
        mainShop = shopRepository.findByShopCode("MAIN-001").orElseGet(() -> 
            shopRepository.save(com.buysell.modules.shop.entity.Shop.builder().shopCode("MAIN-001").name("Main Shop").status(com.buysell.modules.shop.entity.ShopStatus.ACTIVE).build())
        );

        branch1 = branchRepository.save(Branch.builder().name("Branch 1 " + java.util.UUID.randomUUID().toString()).shop(mainShop).isActive(true).build());
        branch2 = branchRepository.save(Branch.builder().name("Branch 2 " + java.util.UUID.randomUUID().toString()).shop(mainShop).isActive(true).build());
        admin = userRepository.save(User.builder().username("test_admin_" + java.util.UUID.randomUUID().toString()).passwordHash("hash").isActive(true).build());

        Customer customer = customerRepository.save(Customer.builder().firstName("Test").lastName("Customer").phone("123").branch(branch1).build());
        device = deviceRepository.save(Device.builder().imei1("12345" + java.util.UUID.randomUUID().toString().substring(0, 10)).brand("Apple").model("iPhone 13").build());

        purchase = PurchaseTransaction.builder()
                .purchaseNumber("PUR-" + java.util.UUID.randomUUID().toString().substring(0, 8))
                .device(device)
                .branch(branch1)
                .employee(admin)
                .customer(customer)
                .finalPrice(new BigDecimal("100.00"))
                .transactionStatus(TransactionStatus.COMPLETED)
                .build();
        purchase = purchaseTransactionRepository.save(purchase);
        
        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(currentUserService.getCurrentUserId()).thenReturn(admin.getId());
        when(currentUserService.getCurrentBranch()).thenReturn(branch1);
        when(currentUserService.hasPermission(any())).thenReturn(true);
        when(currentUserService.hasAccessToBranch(any())).thenReturn(true);
    }

    @Test
    void testPurchaseToInventoryCreation() {
        InventoryItem item = inventoryCreationService.createInventoryFromPurchase(purchase);

        assertNotNull(item.getId());
        assertEquals(InventoryStatus.AVAILABLE, item.getStatus());
        assertEquals(new BigDecimal("100.00"), item.getCostPrice());
        assertEquals(branch1.getId(), item.getBranch().getId());
        assertEquals(purchase.getId(), item.getPurchaseTransaction().getId());
        assertNotNull(item.getStockCode());
        assertTrue(item.getStockCode().startsWith("STK-"));

        // Duplicate creation should be idempotent
        InventoryItem duplicate = inventoryCreationService.createInventoryFromPurchase(purchase);
        assertEquals(item.getId(), duplicate.getId());
    }
    
    @Test
    void testCancelledPurchaseDoesNotCreateInventory() {
        purchase.setTransactionStatus(TransactionStatus.CANCELLED);
        purchase = purchaseTransactionRepository.save(purchase);
        
        Exception exception = assertThrows(BusinessException.class, () -> {
            inventoryCreationService.createInventoryFromPurchase(purchase);
        });
        assertNotNull(exception);
    }

    @Test
    void testInventoryStatusTransitions() {
        InventoryItem item = inventoryCreationService.createInventoryFromPurchase(purchase);

        ChangeInventoryStatusRequest request = new ChangeInventoryStatusRequest();
        request.setNewStatus(InventoryStatus.BLOCKED);
        request.setReason("Testing blocked");

        InventoryResponse response = inventoryService.changeStatus(item.getId(), request);
        assertEquals(InventoryStatus.BLOCKED, response.getStatus());

        // Invalid transition
        request.setNewStatus(InventoryStatus.SOLD);
        Exception exception = assertThrows(BusinessException.class, () -> {
            inventoryService.changeStatus(item.getId(), request);
        });
        assertNotNull(exception);
    }

    @Test
    void testReservationLifecycle() {
        InventoryItem item = inventoryCreationService.createInventoryFromPurchase(purchase);

        ReserveInventoryRequest reserveReq = new ReserveInventoryRequest();
        reserveReq.setReservedUntil(ZonedDateTime.now().plusHours(1));

        InventoryResponse response = inventoryService.reserveInventory(item.getId(), reserveReq);
        assertEquals(InventoryStatus.RESERVED, response.getStatus());

        response = inventoryService.releaseInventory(item.getId());
        assertEquals(InventoryStatus.AVAILABLE, response.getStatus());
    }

    @Test
    void testSellingPriceUpdate() {
        InventoryItem item = inventoryCreationService.createInventoryFromPurchase(purchase);

        UpdateSellingPriceRequest updateReq = new UpdateSellingPriceRequest();
        updateReq.setSellingPrice(new BigDecimal("150.00"));

        InventoryResponse response = inventoryService.updateSellingPrice(item.getId(), updateReq);
        assertEquals(new BigDecimal("150.00"), response.getSellingPrice());
    }

    @Test
    void testStockTransferLifecycle() {
        InventoryItem item = inventoryCreationService.createInventoryFromPurchase(purchase);

        StockTransferRequest transferReq = new StockTransferRequest();
        transferReq.setToBranchId(branch2.getId());
        transferReq.setInventoryItemIds(List.of(item.getId()));
        transferReq.setNotes("Transferring stock");

        StockTransferResponse transfer = stockTransferService.createTransfer(transferReq);
        assertNotNull(transfer.getId());
        assertEquals(TransferStatus.DRAFT, transfer.getStatus());

        transfer = stockTransferService.transitionTransfer(transfer.getId(), TransferStatus.REQUESTED);
        assertEquals(TransferStatus.REQUESTED, transfer.getStatus());

        transfer = stockTransferService.transitionTransfer(transfer.getId(), TransferStatus.APPROVED);
        assertEquals(TransferStatus.APPROVED, transfer.getStatus());

        transfer = stockTransferService.transitionTransfer(transfer.getId(), TransferStatus.IN_TRANSIT);
        assertEquals(TransferStatus.IN_TRANSIT, transfer.getStatus());

        // Verify InventoryItem is now IN_TRANSIT and source branch remains unchanged
        InventoryResponse inTransitItem = inventoryService.getInventoryById(item.getId());
        assertEquals(InventoryStatus.IN_TRANSIT, inTransitItem.getStatus());
        assertEquals(branch1.getId(), inTransitItem.getBranchId()); // Source branch unchanged

        // Verify IN_TRANSIT cannot be reserved
        ReserveInventoryRequest reserveReq = new ReserveInventoryRequest();
        reserveReq.setReservedUntil(java.time.ZonedDateTime.now().plusMinutes(30));
        Exception reserveException = assertThrows(BusinessException.class, () -> inventoryService.reserveInventory(item.getId(), reserveReq));
        assertNotNull(reserveException);

        // Verify IN_TRANSIT cannot be transferred again
        StockTransferRequest secondTransferReq = new StockTransferRequest();
        secondTransferReq.setToBranchId(branch2.getId());
        secondTransferReq.setInventoryItemIds(List.of(item.getId()));
        secondTransferReq.setNotes("Transferring stock again");
        Exception transferException = assertThrows(BusinessException.class, () -> stockTransferService.createTransfer(secondTransferReq));
        assertNotNull(transferException);

        // Simulate completing transfer from branch2 perspective
        when(currentUserService.getCurrentBranch()).thenReturn(branch2);
        
        transfer = stockTransferService.completeTransfer(transfer.getId());
        assertEquals(TransferStatus.COMPLETED, transfer.getStatus());

        InventoryResponse updatedItem = inventoryService.getInventoryById(item.getId());
        assertEquals(branch2.getId(), updatedItem.getBranchId()); // Destination branch becomes owner
        assertEquals(InventoryStatus.AVAILABLE, updatedItem.getStatus()); // transfer completion changes IN_TRANSIT -> AVAILABLE
    }
}
