package com.buysell.modules.purchase.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.entity.CustomerConsent;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.enums.VerificationStatus;
import com.buysell.modules.customer.repository.CustomerConsentRepository;
import com.buysell.modules.customer.service.CustomerService;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.entity.DeviceCondition;
import com.buysell.modules.device.entity.DeviceInspection;
import com.buysell.modules.device.enums.DeviceStatus;
import com.buysell.modules.device.service.DeviceConditionService;
import com.buysell.modules.device.service.DeviceInspectionService;
import com.buysell.modules.device.service.DeviceService;
import com.buysell.modules.purchase.dto.CreatePurchaseRequest;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.repository.PurchaseStatusHistoryRepository;
import com.buysell.modules.purchase.repository.PurchaseTransactionRepository;
import com.buysell.modules.inventory.service.InventoryCreationService;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock private PurchaseTransactionRepository purchaseRepository;
    @Mock private PurchaseStatusHistoryRepository statusHistoryRepository;
    @Mock private CustomerService customerService;
    @Mock private DeviceService deviceService;
    @Mock private DeviceConditionService conditionService;
    @Mock private DeviceInspectionService inspectionService;
    @Mock private CustomerConsentRepository consentRepository;
    @Mock private PurchaseStatusService statusService;
    @Mock private PurchasePricingService pricingService;
    @Mock private PurchasePaymentService paymentService;
    @Mock private PurchaseReceiptService receiptService;
    @Mock private InventoryCreationService inventoryCreationService;
    @Mock private CurrentUserService currentUserService;
    @Mock private AuditService auditService;

    @InjectMocks private PurchaseService purchaseService;

    private User currentUser;
    private Branch branch;
    private Customer customer;
    private Device device;
    private UUID customerId;
    private UUID deviceId;
    private UUID purchaseId;

    @BeforeEach
    void setUp() {
        branch = new Branch();
        branch.setId(UUID.randomUUID());

        currentUser = new User();
        currentUser.setId(UUID.randomUUID());

        customerId = UUID.randomUUID();
        customer = new Customer();
        customer.setId(customerId);
        customer.setStatus(CustomerStatus.ACTIVE);
        customer.setVerificationStatus(VerificationStatus.VERIFIED);

        deviceId = UUID.randomUUID();
        device = new Device();
        device.setId(deviceId);
        device.setStatus(DeviceStatus.ACTIVE);
        
        purchaseId = UUID.randomUUID();
    }

    @Test
    void createPurchase_success() {
        CreatePurchaseRequest request = new CreatePurchaseRequest();
        request.setCustomerId(customerId);
        request.setDeviceId(deviceId);
        request.setSuggestedPrice(new BigDecimal("1000.00"));
        request.setFinalPrice(new BigDecimal("1000.00"));

        when(customerService.getAndValidateAccess(customerId)).thenReturn(customer);
        when(deviceService.getDeviceByIdWithLock(deviceId)).thenReturn(device);
        when(purchaseRepository.hasActivePurchaseForDevice(eq(deviceId), anyList())).thenReturn(false);
        when(purchaseRepository.getNextPurchaseNumberSequence()).thenReturn(1L);
        when(currentUserService.getCurrentBranch()).thenReturn(branch);
        when(currentUserService.getCurrentUser()).thenReturn(currentUser);
        when(currentUserService.getCurrentUserId()).thenReturn(currentUser.getId());
        when(purchaseRepository.save(any())).thenAnswer(i -> {
            PurchaseTransaction p = i.getArgument(0);
            p.setId(purchaseId);
            return p;
        });

        PurchaseTransaction result = purchaseService.createPurchase(request);

        assertNotNull(result);
        assertEquals("PUR-" + java.time.Year.now().getValue() + "-000001", result.getPurchaseNumber());
        assertEquals(TransactionStatus.INITIATED, result.getTransactionStatus());
        verify(purchaseRepository).save(any());
        verify(auditService).logAction(any(), any(), anyString(), anyString(), any(), any(), any(), any(), anyString());
    }

    @Test
    void createPurchase_activePurchaseExists_throwsException() {
        CreatePurchaseRequest request = new CreatePurchaseRequest();
        request.setCustomerId(customerId);
        request.setDeviceId(deviceId);

        when(customerService.getAndValidateAccess(customerId)).thenReturn(customer);
        when(deviceService.getDeviceByIdWithLock(deviceId)).thenReturn(device);
        when(purchaseRepository.hasActivePurchaseForDevice(eq(deviceId), anyList())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> purchaseService.createPurchase(request));
        assertEquals("PURCHASE_ACTIVE", ex.getCode());
    }

    @Test
    void completePurchase_success() {
        PurchaseTransaction purchase = new PurchaseTransaction();
        purchase.setId(purchaseId);
        purchase.setTransactionStatus(TransactionStatus.PENDING_PAYMENT);
        purchase.setCustomer(customer);
        purchase.setDevice(device);
        purchase.setBranch(branch);
        purchase.setFinalPrice(new BigDecimal("1000.00"));

        when(purchaseRepository.findByIdWithLock(purchaseId)).thenReturn(Optional.of(purchase));
        when(currentUserService.hasPermission("SUPER_ADMIN")).thenReturn(true);
        when(currentUserService.getCurrentUserId()).thenReturn(currentUser.getId());
        
        when(conditionService.getConditionHistory(deviceId)).thenReturn(List.of(new DeviceCondition()));
        when(inspectionService.getInspectionHistory(deviceId)).thenReturn(List.of(new DeviceInspection()));
        when(consentRepository.findByReferenceTypeAndReferenceId("PURCHASE", purchaseId)).thenReturn(List.of(new CustomerConsent()));
        when(paymentService.calculateTotalSuccessfulPayments(purchaseId)).thenReturn(new BigDecimal("1000.00"));
        
        when(purchaseRepository.save(any())).thenReturn(purchase);

        assertDoesNotThrow(() -> purchaseService.completePurchase(purchaseId));
        
        assertEquals(TransactionStatus.COMPLETED, purchase.getTransactionStatus());
        verify(receiptService).generateReceipt(purchase);
    }
}
