package com.buysell.modules.inventory;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import com.buysell.exception.BusinessException;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.repository.BranchRepository;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.repository.DeviceRepository;
import com.buysell.modules.inventory.dto.ReserveInventoryRequest;
import com.buysell.modules.inventory.dto.StockTransferRequest;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.enums.TransferStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.inventory.repository.StockTransferRepository;
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
public class InventoryConcurrencyTest {

    @Autowired
    private InventoryCreationService inventoryCreationService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockTransferService stockTransferService;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private com.buysell.modules.shop.repository.ShopRepository shopRepository;

    @Autowired
    private StockTransferRepository stockTransferRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private PurchaseTransactionRepository purchaseTransactionRepository;

    @MockBean
    private CurrentUserService currentUserService;

    private Branch branch1;
    private Branch branch2;
    private User admin;
    private Device device;
    private PurchaseTransaction purchase;
    private com.buysell.modules.shop.entity.Shop mainShop;

    @BeforeEach
    public void setUp() {
        stockTransferRepository.deleteAll();
        inventoryItemRepository.deleteAll();
        purchaseTransactionRepository.deleteAll();
        deviceRepository.deleteAll();
        customerRepository.deleteAll();
        branchRepository.deleteAll();
        userRepository.deleteAll();

        mainShop = shopRepository.findByShopCode("MAIN-001").orElseGet(() -> 
            shopRepository.save(com.buysell.modules.shop.entity.Shop.builder().shopCode("MAIN-001").name("Main Shop").status(com.buysell.modules.shop.entity.ShopStatus.ACTIVE).build())
        );

        branch1 = branchRepository.save(Branch.builder().name("Branch 1 " + java.util.UUID.randomUUID().toString()).shop(mainShop).isActive(true).build());
        branch2 = branchRepository.save(Branch.builder().name("Branch 2 " + java.util.UUID.randomUUID().toString()).shop(mainShop).isActive(true).build());
        admin = userRepository.save(User.builder().username("test_admin_" + java.util.UUID.randomUUID().toString()).passwordHash("hash").isActive(true).build());

        when(currentUserService.getCurrentBranch()).thenReturn(branch1);
        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(currentUserService.getCurrentUserId()).thenReturn(admin.getId());
        when(currentUserService.hasPermission(any())).thenReturn(true);
        when(currentUserService.hasAccessToBranch(any())).thenReturn(true);

        Customer customer = customerRepository.save(Customer.builder().firstName("Test").lastName("Customer").phone("123").branch(branch1).build());
        device = deviceRepository.save(Device.builder().imei1("123456789012345").brand("Apple").model("iPhone 13").build());

        purchase = PurchaseTransaction.builder()
                .purchaseNumber("PUR-123")
                .branch(branch1)
                .employee(admin)
                .customer(customer)
                .device(device)
                .transactionStatus(TransactionStatus.COMPLETED)
                .finalPrice(new BigDecimal("500.00"))
                .build();
        purchase = purchaseTransactionRepository.save(purchase);
    }

    @Test
    public void testConcurrentInventoryCreation() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    inventoryCreationService.createInventoryFromPurchase(purchase);
                    successCount.incrementAndGet();
                } catch (BusinessException | org.springframework.dao.DataIntegrityViolationException | org.hibernate.exception.ConstraintViolationException e) {
                    exceptionCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        // Since the method is idempotent, some threads might catch DataIntegrityViolationException 
        // while others might get the already created item and succeed.
        // Therefore, we only care that exactly one item was created in the DB.
        assertEquals(1, inventoryItemRepository.count(), "Exactly one inventory item should exist");
    }

    @Test
    public void testConcurrentStockCodeGeneration() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();
        
        List<PurchaseTransaction> distinctPurchases = new ArrayList<>();
        for(int i = 0; i < threadCount; i++) {
            Device newDevice = deviceRepository.save(Device.builder().imei1("100000000000" + i).brand("Brand").model("Model").build());
            PurchaseTransaction p = PurchaseTransaction.builder()
                .purchaseNumber("PUR-SC-" + i)
                .branch(branch1)
                .employee(admin)
                .customer(purchase.getCustomer())
                .device(newDevice)
                .transactionStatus(TransactionStatus.COMPLETED)
                .finalPrice(new BigDecimal("500.00"))
                .build();
            distinctPurchases.add(purchaseTransactionRepository.save(p));
        }

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final PurchaseTransaction currentPurchase = distinctPurchases.get(i);
            tasks.add(() -> {
                try {
                    inventoryCreationService.createInventoryFromPurchase(currentPurchase);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        assertEquals(threadCount, successCount.get(), "All creations should succeed");
        assertEquals(0, exceptionCount.get(), "No exceptions should occur");
        assertEquals(threadCount, inventoryItemRepository.count(), "Should have the threadCount items");
    }

    @Test
    public void testConcurrentReservation() throws InterruptedException {
        inventoryCreationService.createInventoryFromPurchase(purchase);
        InventoryItem item = inventoryItemRepository.findAll().get(0);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        ReserveInventoryRequest request = new ReserveInventoryRequest();
        request.setReservedUntil(ZonedDateTime.now().plusDays(1));

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    inventoryService.reserveInventory(item.getId(), request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only one reservation should succeed");
        assertEquals(threadCount - 1, exceptionCount.get(), "Others should fail");
    }

    @Test
    public void testConcurrentTransferCreation() throws InterruptedException {
        inventoryCreationService.createInventoryFromPurchase(purchase);
        InventoryItem item = inventoryItemRepository.findAll().get(0);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        StockTransferRequest request = new StockTransferRequest();
        request.setToBranchId(branch2.getId());
        request.setInventoryItemIds(List.of(item.getId()));
        request.setNotes("Concurrent transfer");

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    stockTransferService.createTransfer(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only one transfer creation should succeed");
        assertEquals(threadCount - 1, exceptionCount.get(), "Others should fail");
    }

    @Test
    public void testTransferVsReservationRace() throws InterruptedException, ExecutionException {
        inventoryCreationService.createInventoryFromPurchase(purchase);
        InventoryItem item = inventoryItemRepository.findAll().get(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        StockTransferRequest transferRequest = new StockTransferRequest();
        transferRequest.setToBranchId(branch2.getId());
        transferRequest.setInventoryItemIds(List.of(item.getId()));
        transferRequest.setNotes("Test Transfer");
        ReserveInventoryRequest reserveRequest = new ReserveInventoryRequest();
        reserveRequest.setReservedUntil(ZonedDateTime.now().plusDays(1));

        Callable<Boolean> transferTask = () -> {
            try {
                stockTransferService.createTransfer(transferRequest);
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Callable<Boolean> reserveTask = () -> {
            try {
                inventoryService.reserveInventory(item.getId(), reserveRequest);
                return true;
            } catch (Exception e) {
                return false;
            }
        };

        Future<Boolean> transferFuture = executor.submit(transferTask);
        Future<Boolean> reserveFuture = executor.submit(reserveTask);

        boolean transferSuccess = transferFuture.get();
        boolean reserveSuccess = reserveFuture.get();
        
        executor.shutdown();

        assertTrue(transferSuccess || reserveSuccess, "At least one operation should succeed");
        assertTrue(!(transferSuccess && reserveSuccess), "Both operations cannot succeed concurrently");
    }

    @Test
    public void testConcurrentTransferCompletion() throws InterruptedException {
        inventoryCreationService.createInventoryFromPurchase(purchase);
        InventoryItem item = inventoryItemRepository.findAll().get(0);
        
        StockTransferRequest request = new StockTransferRequest();
        request.setToBranchId(branch2.getId());
        request.setInventoryItemIds(List.of(item.getId()));
        request.setNotes("Test Transfer");
        var createResponse = stockTransferService.createTransfer(request);
        stockTransferService.transitionTransfer(createResponse.getId(), TransferStatus.REQUESTED);
        stockTransferService.transitionTransfer(createResponse.getId(), TransferStatus.APPROVED);
        stockTransferService.transitionTransfer(createResponse.getId(), TransferStatus.IN_TRANSIT);

        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Callable<Void>> tasks = new ArrayList<>();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);
        
        when(currentUserService.getCurrentBranch()).thenReturn(branch2);

        for (int i = 0; i < threadCount; i++) {
            tasks.add(() -> {
                try {
                    stockTransferService.completeTransfer(createResponse.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                }
                return null;
            });
        }

        executor.invokeAll(tasks);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only one completion should succeed");
        assertEquals(threadCount - 1, exceptionCount.get(), "Others should fail");
        
        InventoryItem updatedItem = inventoryItemRepository.findById(item.getId()).get();
        assertEquals(branch2.getId(), updatedItem.getBranch().getId());
        assertEquals(InventoryStatus.AVAILABLE, updatedItem.getStatus());
    }
}
