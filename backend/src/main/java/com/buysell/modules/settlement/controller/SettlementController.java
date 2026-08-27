package com.buysell.modules.settlement.controller;

import com.buysell.modules.settlement.dto.*;
import com.buysell.modules.settlement.entity.ReconciliationRecord;
import com.buysell.modules.settlement.entity.SettlementDispute;
import com.buysell.modules.settlement.entity.SettlementPayment;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.modules.settlement.service.SettlementDisputeService;
import com.buysell.modules.settlement.service.SettlementPaymentService;
import com.buysell.modules.settlement.service.SettlementReconciliationService;
import com.buysell.security.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    @org.springframework.beans.factory.annotation.Autowired

    private ShopSettlementRepository settlementRepository;
    @org.springframework.beans.factory.annotation.Autowired
    private SettlementPaymentService paymentService;
    @org.springframework.beans.factory.annotation.Autowired
    private SettlementReconciliationService reconciliationService;
    @org.springframework.beans.factory.annotation.Autowired
    private SettlementDisputeService disputeService;
    @org.springframework.beans.factory.annotation.Autowired
    private CurrentUserService currentUserService;

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_SETTLEMENTS') or hasAuthority('VIEW_OWN_SETTLEMENTS')")
    public ResponseEntity<Page<ShopSettlement>> listSettlements(
            @RequestParam(required = false) UUID shopId,
            Pageable pageable) {
        
        if (currentUserService.isSuperAdmin()) {
            if (shopId != null) {
                return ResponseEntity.ok(settlementRepository.findBySourceShopIdOrDestinationShopId(shopId, shopId, pageable));
            }
            return ResponseEntity.ok(settlementRepository.findAll(pageable));
        }

        UUID currentUserShopId = currentUserService.getCurrentShop().getId();
        return ResponseEntity.ok(settlementRepository.findBySourceShopIdOrDestinationShopId(currentUserShopId, currentUserShopId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_SETTLEMENTS') or hasAuthority('VIEW_OWN_SETTLEMENTS')")
    public ResponseEntity<ShopSettlement> getSettlement(@PathVariable UUID id) {
        ShopSettlement settlement = settlementRepository.findById(id).orElseThrow();
        // check access
        if (!currentUserService.isSuperAdmin()) {
            UUID currentUserShopId = currentUserService.getCurrentShop().getId();
            if (!settlement.getSourceShop().getId().equals(currentUserShopId) && !settlement.getDestinationShop().getId().equals(currentUserShopId)) {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.ok(settlement);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('VIEW_SETTLEMENTS') or hasAuthority('VIEW_OWN_SETTLEMENTS')")
    public ResponseEntity<ShopLedgerSummaryResponse> getSummary(@RequestParam(required = false) UUID shopId) {
        UUID targetShopId = shopId;
        if (!currentUserService.isSuperAdmin()) {
            targetShopId = currentUserService.getCurrentShop().getId();
        }

        BigDecimal receivable = settlementRepository.sumReceivableForShop(targetShopId);
        BigDecimal payable = settlementRepository.sumPayableForShop(targetShopId);
        BigDecimal netBalance = receivable.subtract(payable);

        return ResponseEntity.ok(new ShopLedgerSummaryResponse(receivable, payable, netBalance));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('CREATE_SETTLEMENT_PAYMENT')")
    public ResponseEntity<SettlementPayment> createPayment(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSettlementPaymentRequest request) {
        
        SettlementPayment payment = paymentService.createPayment(
                id, request.getAmount(), request.getPaymentMode(), 
                request.getReferenceNumber(), request.getIdempotencyKey());
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/{id}/reconcile")
    @PreAuthorize("hasAuthority('RECONCILE_SETTLEMENT')")
    public ResponseEntity<ReconciliationRecord> reconcile(
            @PathVariable UUID id,
            @Valid @RequestBody CreateReconciliationRequest request) {
        
        ReconciliationRecord record = reconciliationService.reconcileSettlement(
                id, request.getShopId(), request.getActualAmount(), request.getNotes());
        return ResponseEntity.ok(record);
    }

    @PostMapping("/{id}/disputes")
    @PreAuthorize("hasAuthority('RAISE_SETTLEMENT_DISPUTE')")
    public ResponseEntity<SettlementDispute> raiseDispute(
            @PathVariable UUID id,
            @Valid @RequestBody CreateSettlementDisputeRequest request) {
        
        SettlementDispute dispute = disputeService.raiseDispute(id, request.getReason(), request.getClaimedAmount());
        return ResponseEntity.ok(dispute);
    }

    @PostMapping("/disputes/{disputeId}/resolve")
    @PreAuthorize("hasAuthority('RESOLVE_SETTLEMENT_DISPUTE')")
    public ResponseEntity<SettlementDispute> resolveDispute(
            @PathVariable UUID disputeId,
            @Valid @RequestBody ResolveDisputeRequest request) {
        
        SettlementDispute dispute = disputeService.resolveDispute(disputeId, request.getResolution());
        return ResponseEntity.ok(dispute);
    }
}
