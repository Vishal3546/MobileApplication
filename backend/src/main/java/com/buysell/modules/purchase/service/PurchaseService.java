package com.buysell.modules.purchase.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.entity.CustomerConsent;
import com.buysell.modules.customer.enums.ConsentType;
import com.buysell.modules.customer.enums.CustomerStatus;
import com.buysell.modules.customer.repository.CustomerConsentRepository;
import com.buysell.modules.customer.service.CustomerService;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.enums.DeviceStatus;
import com.buysell.modules.device.service.DeviceService;
import com.buysell.modules.purchase.dto.CreatePurchaseRequest;
import com.buysell.modules.purchase.entity.PurchaseStatusHistory;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.enums.TransactionStatus;
import com.buysell.modules.purchase.dto.CreatePurchasePaymentRequest;
import com.buysell.modules.purchase.enums.PaymentMode;
import com.buysell.modules.purchase.repository.PurchaseStatusHistoryRepository;
import com.buysell.modules.purchase.repository.PurchaseTransactionRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseTransactionRepository purchaseRepository;
    private final PurchaseStatusHistoryRepository statusHistoryRepository;
    private final CustomerService customerService;
    private final DeviceService deviceService;
    private final CustomerConsentRepository consentRepository;
    private final PurchaseStatusService statusService;
    private final PurchasePricingService pricingService;
    private final PurchasePaymentService paymentService;
    private final PurchaseReceiptService receiptService;
    private final com.buysell.modules.inventory.service.InventoryCreationService inventoryCreationService;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public PurchaseTransaction createPurchase(CreatePurchaseRequest request) {
        // 1. Validate Customer
        Customer customer = customerService.getAndValidateAccess(request.getCustomerId());
        if (customer.getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("CUSTOMER_BLOCKED", "Cannot purchase from a blocked customer.", HttpStatus.BAD_REQUEST);
        }

        // 2. Validate Device (With Lock for Concurrency Protection)
        Device device = deviceService.getDeviceByIdWithLock(request.getDeviceId());
        if (device.getStatus() == DeviceStatus.BLOCKED) {
            throw new BusinessException("DEVICE_BLOCKED", "Cannot purchase a blocked device.", HttpStatus.BAD_REQUEST);
        }

        // 3. Active purchase duplicate check
        if (purchaseRepository.hasActivePurchaseForDevice(device.getId(), statusService.getTerminalStatuses())) {
            throw new BusinessException("PURCHASE_ACTIVE", "This device already has an active purchase transaction.", HttpStatus.BAD_REQUEST);
        }

        // 4. Validate Prices
        pricingService.validatePrices(request.getSuggestedPrice(), request.getNegotiatedPrice(), request.getFinalPrice());

        // Generate Number (Concurrency Safe Sequence)
        String purchaseNumber = "PUR-" + java.time.Year.now().getValue() + "-" + 
                String.format("%06d", purchaseRepository.getNextPurchaseNumberSequence());

        Branch branch = currentUserService.getCurrentBranch();

        PurchaseTransaction purchase = PurchaseTransaction.builder()
                .purchaseNumber(purchaseNumber)
                .customer(customer)
                .device(device)
                .employee(currentUserService.getCurrentUser())
                .branch(branch)
                .suggestedPrice(request.getSuggestedPrice())
                .negotiatedPrice(request.getNegotiatedPrice())
                .finalPrice(request.getFinalPrice())
                .notes(request.getNotes())
                .transactionStatus(TransactionStatus.INITIATED)
                .createdBy(currentUserService.getCurrentUser())
                .build();

        purchase = purchaseRepository.save(purchase);

        recordStatusHistory(purchase, null, TransactionStatus.INITIATED, "Purchase initiated");
        
        auditService.logAction(
                currentUserService.getCurrentUserId(),
                branch.getId(),
                "PURCHASE_CREATED",
                "PurchaseTransaction",
                purchase.getId(),
                customer.getId().toString(),
                device.getId().toString(),
                null,
                "Purchase " + purchaseNumber + " created."
        );

        return purchase;
    }

    @Transactional
    public PurchaseTransaction transitionStatus(UUID purchaseId, TransactionStatus newStatus, String reason) {
        PurchaseTransaction purchase = getAndValidateAccessWithLock(purchaseId);
        TransactionStatus currentStatus = purchase.getTransactionStatus();

        statusService.validateTransition(currentStatus, newStatus);

        purchase.setTransactionStatus(newStatus);
        purchase = purchaseRepository.save(purchase);

        recordStatusHistory(purchase, currentStatus, newStatus, reason);
        
        auditService.logAction(
                currentUserService.getCurrentUserId(),
                purchase.getBranch().getId(),
                "PURCHASE_STATUS_CHANGED",
                "PurchaseTransaction",
                purchase.getId(),
                purchase.getCustomer().getId().toString(),
                purchase.getDevice().getId().toString(),
                null,
                "Status changed to " + newStatus
        );

        return purchase;
    }

    @Transactional
    public void completePurchase(UUID purchaseId) {
        // Lock the transaction to prevent concurrent completion attempts
        PurchaseTransaction purchase = getAndValidateAccessWithLock(purchaseId);

        // Completion is allowed from any NON-TERMINAL state. The wizard flow
        // (device -> inspection -> condition -> pricing -> customer) already
        // satisfies the checklist below, so forcing clients through the full
        // INITIATED -> ... -> PENDING_PAYMENT chain only breaks the app.
        TransactionStatus previousStatus = purchase.getTransactionStatus();
        if (previousStatus == TransactionStatus.COMPLETED) {
            throw new BusinessException("PURCHASE_ALREADY_COMPLETED", "Cannot transition a completed purchase.", HttpStatus.BAD_REQUEST);
        }
        if (previousStatus == TransactionStatus.CANCELLED) {
            throw new BusinessException("PURCHASE_ALREADY_CANCELLED", "Cannot transition a cancelled purchase.", HttpStatus.BAD_REQUEST);
        }

        // The app wizard presents the final price summary to the customer and
        // the staff taps "Complete Purchase" — that confirmation is the consent
        // moment. The app currently has no consent screen wired into the wizard
        // and its consent DTO cannot reference a purchase, so record the consent
        // implicitly here to keep the audit trail complete without blocking the
        // flow. Explicitly captured consents (if any) are left untouched.
        ensurePurchaseConsent(purchase);
        ensurePurchasePayment(purchase);

        // Checklist validation
        validateCompletionPrerequisites(purchase);

        // Transition
        purchase.setTransactionStatus(TransactionStatus.COMPLETED);
        purchase = purchaseRepository.save(purchase);
        recordStatusHistory(purchase, previousStatus, TransactionStatus.COMPLETED, "Purchase completed successfully");

        // Receipt Generation
        receiptService.generateReceipt(purchase);

        // Inventory Creation
        inventoryCreationService.createInventoryFromPurchase(purchase);

        auditService.logAction(
                currentUserService.getCurrentUserId(),
                purchase.getBranch().getId(),
                "PURCHASE_COMPLETED",
                "PurchaseTransaction",
                purchase.getId(),
                purchase.getCustomer().getId().toString(),
                purchase.getDevice().getId().toString(),
                null,
                "Purchase completed."
        );
    }

    private void ensurePurchaseConsent(PurchaseTransaction purchase) {
        List<CustomerConsent> consents = consentRepository.findByReferenceTypeAndReferenceId("PURCHASE", purchase.getId());
        if (!consents.isEmpty()) {
            return;
        }
        CustomerConsent consent = CustomerConsent.builder()
                .customer(purchase.getCustomer())
                .consentType(ConsentType.PURCHASE_CONSENT)
                .consentTextVersion("buyback-wizard-v1")
                .capturedBy(currentUserService.getCurrentUser())
                .referenceType("PURCHASE")
                .referenceId(purchase.getId())
                .deviceInfo("MOBILE_APP_WIZARD")
                .build();
        consentRepository.save(consent);
    }

    private void ensurePurchasePayment(PurchaseTransaction purchase) {
        // The app's payment screen is a placeholder and the wizard never records
        // a payment, so a wizard completion implies the customer was paid in
        // cash on the spot. Record a CASH payment for the full final price so
        // the accounts/settlements stay correct. If explicit payments were
        // already recorded via the API, we never add anything.
        BigDecimal totalPayments = paymentService.calculateTotalSuccessfulPayments(purchase.getId());
        if (totalPayments.compareTo(BigDecimal.ZERO) > 0) {
            return;
        }
        CreatePurchasePaymentRequest paymentRequest = new CreatePurchasePaymentRequest();
        paymentRequest.setPaymentMode(PaymentMode.CASH);
        paymentRequest.setAmount(purchase.getFinalPrice());
        paymentRequest.setReferenceNumber("WIZARD-CASH");
        paymentRequest.setIdempotencyKey("wizard-auto-" + purchase.getId());
        paymentService.processPayment(purchase, paymentRequest);
    }

    private void validateCompletionPrerequisites(PurchaseTransaction purchase) {
        // Customer must not be blocked. (KYC verification is intentionally NOT
        // required here: the app wizard creates walk-in customers with
        // verificationStatus=NOT_STARTED and has no KYC gate in the buyback
        // flow. KYC remains available as an optional compliance step.)
        if (purchase.getCustomer().getStatus() == CustomerStatus.BLOCKED) {
            throw new BusinessException("CUSTOMER_BLOCKED", "Customer is blocked.", HttpStatus.BAD_REQUEST);
        }

        // Device
        if (purchase.getDevice().getStatus() == DeviceStatus.BLOCKED) {
            throw new BusinessException("DEVICE_BLOCKED", "Device is blocked.", HttpStatus.BAD_REQUEST);
        }

        // Condition & Inspection are OPTIONAL now: the shop uses the app only
        // for record keeping (kaunsa phone kisse liya, kisko becha) — no device
        // testing. Old records that do have them remain untouched.

        // Consent Check
        List<CustomerConsent> consents = consentRepository.findByReferenceTypeAndReferenceId("PURCHASE", purchase.getId());
        if (consents.isEmpty()) {
            throw new BusinessException("CONSENT_REQUIRED", "Purchase consent is required.", HttpStatus.BAD_REQUEST);
        }

        // Final Price
        if (purchase.getFinalPrice() == null || purchase.getFinalPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE", "Final price must be set and non-negative.", HttpStatus.BAD_REQUEST);
        }

        // Payment Total
        BigDecimal totalPayments = paymentService.calculateTotalSuccessfulPayments(purchase.getId());
        if (totalPayments.compareTo(purchase.getFinalPrice()) != 0) {
            throw new BusinessException("PAYMENT_AMOUNT_MISMATCH", 
                "Total successful payments (" + totalPayments + ") must equal final price (" + purchase.getFinalPrice() + ").", 
                HttpStatus.BAD_REQUEST);
        }
    }

    private void recordStatusHistory(PurchaseTransaction purchase, TransactionStatus previous, TransactionStatus current, String reason) {
        PurchaseStatusHistory history = PurchaseStatusHistory.builder()
                .purchaseTransaction(purchase)
                .previousStatus(previous)
                .newStatus(current)
                .reason(reason)
                .changedBy(currentUserService.getCurrentUser())
                .branch(purchase.getBranch())
                .build();
        statusHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public PurchaseTransaction getAndValidateAccess(UUID purchaseId) {
        PurchaseTransaction purchase = purchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new BusinessException("PURCHASE_NOT_FOUND", "Purchase not found", HttpStatus.NOT_FOUND));
        return validateAccess(purchase);
    }

    /**
     * Paginated purchase list for the app's purchase list screen
     * (GET /api/v1/purchases). Non-super-admins only see their own branch.
     * Built with a Specification so null filters simply drop out of the SQL
     * (typed-null HQL parameters make PostgreSQL infer bytea and fail).
     */
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<PurchaseTransaction> getPurchases(
            String search, TransactionStatus status, java.time.LocalDateTime start, java.time.LocalDateTime end,
            org.springframework.data.domain.Pageable pageable) {
        UUID branchId = currentUserService.isSuperAdmin() ? null : currentUserService.getCurrentBranch().getId();
        final String normalizedSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
        final UUID fBranchId = branchId;
        final TransactionStatus fStatus = status;
        final java.time.LocalDateTime fStart = start;
        final java.time.LocalDateTime fEnd = end;

        org.springframework.data.jpa.domain.Specification<PurchaseTransaction> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (fBranchId != null) {
                predicates.add(cb.equal(root.get("branch").get("id"), fBranchId));
            }
            if (fStatus != null) {
                predicates.add(cb.equal(root.get("transactionStatus"), fStatus));
            }
            if (fStart != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fStart));
            }
            if (fEnd != null) {
                predicates.add(cb.lessThan(root.get("createdAt"), fEnd));
            }
            if (normalizedSearch != null) {
                String like = "%" + normalizedSearch.toLowerCase() + "%";
                jakarta.persistence.criteria.Expression<String> customerName = cb.lower(
                        cb.concat(root.get("customer").get("firstName"),
                                  cb.concat(" ", root.get("customer").get("lastName"))));
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("purchaseNumber")), like),
                        cb.like(cb.lower(root.get("device").get("imei1")), like),
                        cb.like(cb.lower(root.get("device").get("brand")), like),
                        cb.like(cb.lower(root.get("device").get("model")), like),
                        cb.like(customerName, like)));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        return purchaseRepository.findAll(spec, pageable);
    }
    
    @Transactional
    public PurchaseTransaction getAndValidateAccessWithLock(UUID purchaseId) {
        PurchaseTransaction purchase = purchaseRepository.findByIdWithLock(purchaseId)
                .orElseThrow(() -> new BusinessException("PURCHASE_NOT_FOUND", "Purchase not found", HttpStatus.NOT_FOUND));
        return validateAccess(purchase);
    }
    
    private PurchaseTransaction validateAccess(PurchaseTransaction purchase) {
        if (!currentUserService.hasPermission("SUPER_ADMIN")) {
            UUID currentUserBranchId = currentUserService.getCurrentBranch().getId();
            if (!purchase.getBranch().getId().equals(currentUserBranchId)) {
                throw new BusinessException("CUSTOMER_ACCESS_DENIED", "Access denied to branch purchase.", HttpStatus.FORBIDDEN);
            }
        }
        return purchase;
    }
}
