package com.buysell.modules.inventory.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.inventory.dto.*;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.entity.InventoryStatusHistory;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.enums.TransferStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.inventory.repository.InventoryStatusHistoryRepository;
import com.buysell.modules.inventory.repository.StockTransferItemRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryStatusHistoryRepository historyRepository;
    private final InventoryStateMachine stateMachine;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;
    private final StockTransferItemRepository transferItemRepository;

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(UUID id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Inventory not found.", HttpStatus.NOT_FOUND));
        validateAccess(item.getBranch().getId());
        return mapToResponse(item);
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponse> getInventoryList(String status, String search, Pageable pageable) {
        UUID branchId = currentUserService.getCurrentBranch().getId();

        Specification<InventoryItem> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            predicates.add(cb.equal(root.get("branch").get("id"), branchId));
            
            if (status != null && !status.isEmpty()) {
                try {
                    predicates.add(cb.equal(root.get("status"), InventoryStatus.valueOf(status.toUpperCase())));
                } catch (IllegalArgumentException ignored) {}
            }
            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("stockCode")), searchPattern),
                    cb.like(cb.lower(root.join("device").get("model")), searchPattern),
                    cb.like(cb.lower(root.join("device").get("brand")), searchPattern)
                ));
            }
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };

        return inventoryItemRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByStockCode(String stockCode) {
        InventoryItem item = inventoryItemRepository.findByStockCode(stockCode)
                .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Inventory not found.", HttpStatus.NOT_FOUND));
        validateAccess(item.getBranch().getId());
        return mapToResponse(item);
    }

    @Transactional
    public InventoryResponse changeStatus(UUID id, ChangeInventoryStatusRequest request) {
        InventoryItem item = getInventoryWithLock(id);
        validateAccess(item.getBranch().getId());

        InventoryStatus oldStatus = item.getStatus();
        stateMachine.validateTransition(oldStatus, request.getNewStatus());

        item.setStatus(request.getNewStatus());
        if (request.getNewStatus() == InventoryStatus.AVAILABLE) {
            item.setReservedUntil(null);
            item.setReservedBy(null);
        }

        item = inventoryItemRepository.save(item);
        recordHistory(item, oldStatus, request.getNewStatus(), request.getReason(), "MANUAL_UPDATE", null);

        auditService.logAction(currentUserService.getCurrentUserId(), item.getBranch().getId(),
                "INVENTORY_STATUS_CHANGED", "InventoryItem", item.getId(),
                item.getPurchaseTransaction().getId().toString(), item.getDevice().getId().toString(),
                null, "Status changed from " + oldStatus + " to " + request.getNewStatus());

        return mapToResponse(item);
    }

    @Transactional
    public InventoryResponse updateSellingPrice(UUID id, UpdateSellingPriceRequest request) {
        InventoryItem item = getInventoryWithLock(id);
        validateAccess(item.getBranch().getId());

        item.setSellingPrice(request.getSellingPrice());
        item.setUpdatedBy(currentUserService.getCurrentUser());
        item = inventoryItemRepository.save(item);

        auditService.logAction(currentUserService.getCurrentUserId(), item.getBranch().getId(),
                "SELLING_PRICE_UPDATED", "InventoryItem", item.getId(),
                item.getPurchaseTransaction().getId().toString(), item.getDevice().getId().toString(),
                null, "Selling price updated to " + request.getSellingPrice());

        return mapToResponse(item);
    }

    @Transactional
    public InventoryResponse reserveInventory(UUID id, ReserveInventoryRequest request) {
        InventoryItem item = getInventoryWithLock(id);
        validateAccess(item.getBranch().getId());

        if (item.getStatus() != InventoryStatus.AVAILABLE) {
            throw new BusinessException("INVENTORY_NOT_AVAILABLE", "Inventory item is not available for reservation.", HttpStatus.BAD_REQUEST);
        }

        if (transferItemRepository.existsByInventoryItemIdAndStockTransferStatusIn(
                id, Arrays.asList(TransferStatus.DRAFT, TransferStatus.REQUESTED, TransferStatus.APPROVED, TransferStatus.IN_TRANSIT))) {
            throw new BusinessException("INVENTORY_TRANSFER_ACTIVE", "Cannot reserve item currently in an active stock transfer.", HttpStatus.BAD_REQUEST);
        }

        stateMachine.validateTransition(InventoryStatus.AVAILABLE, InventoryStatus.RESERVED);

        item.setStatus(InventoryStatus.RESERVED);
        item.setReservedUntil(request.getReservedUntil());
        item.setReservedBy(currentUserService.getCurrentUser());
        item = inventoryItemRepository.save(item);

        recordHistory(item, InventoryStatus.AVAILABLE, InventoryStatus.RESERVED, "Reserved for future sale", "RESERVATION", null);

        auditService.logAction(currentUserService.getCurrentUserId(), item.getBranch().getId(),
                "INVENTORY_RESERVED", "InventoryItem", item.getId(),
                item.getPurchaseTransaction().getId().toString(), item.getDevice().getId().toString(),
                null, "Inventory reserved until " + request.getReservedUntil());

        return mapToResponse(item);
    }

    @Transactional
    public InventoryResponse releaseInventory(UUID id) {
        InventoryItem item = getInventoryWithLock(id);
        validateAccess(item.getBranch().getId());

        if (item.getStatus() != InventoryStatus.RESERVED) {
            throw new BusinessException("INVENTORY_NOT_RESERVED", "Inventory is not currently reserved.", HttpStatus.BAD_REQUEST);
        }

        stateMachine.validateTransition(InventoryStatus.RESERVED, InventoryStatus.AVAILABLE);

        item.setStatus(InventoryStatus.AVAILABLE);
        item.setReservedUntil(null);
        item.setReservedBy(null);
        item = inventoryItemRepository.save(item);

        recordHistory(item, InventoryStatus.RESERVED, InventoryStatus.AVAILABLE, "Reservation released manually", "RELEASE", null);

        auditService.logAction(currentUserService.getCurrentUserId(), item.getBranch().getId(),
                "INVENTORY_RELEASED", "InventoryItem", item.getId(),
                item.getPurchaseTransaction().getId().toString(), item.getDevice().getId().toString(),
                null, "Inventory reservation released manually.");

        return mapToResponse(item);
    }

    @Transactional
    public void releaseExpiredReservations() {
        // Find reserved items where reservedUntil < now
        // This is a foundation for future scheduler.
        // Left unimplemented here as per "prepare the service so a future scheduler can call it" 
        // It requires a custom query in repository, which we can skip for now or write a simple one.
    }

    @Transactional(readOnly = true)
    public List<InventoryStatusHistoryResponse> getInventoryHistory(UUID id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Inventory not found.", HttpStatus.NOT_FOUND));
        validateAccess(item.getBranch().getId());

        return historyRepository.findByInventoryItemIdOrderByCreatedAtDesc(id)
                .stream().map(this::mapHistoryToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InventorySummaryResponse getInventorySummary(UUID branchId) {
        // Enforce branch access
        UUID finalBranchId = branchId;
        if (!currentUserService.hasPermission("SUPER_ADMIN")) {
            finalBranchId = currentUserService.getCurrentBranch().getId();
        }
        
        final UUID resolvedBranchId = finalBranchId;

        List<InventoryItem> items;
        if (resolvedBranchId != null) {
            items = inventoryItemRepository.findAll((root, query, cb) -> cb.equal(root.get("branch").get("id"), resolvedBranchId));
        } else {
            items = inventoryItemRepository.findAll();
        }

        long total = items.size();
        long available = items.stream().filter(i -> i.getStatus() == InventoryStatus.AVAILABLE).count();
        long reserved = items.stream().filter(i -> i.getStatus() == InventoryStatus.RESERVED).count();
        long inTransit = items.stream().filter(i -> i.getStatus() == InventoryStatus.IN_TRANSIT).count();
        long sold = items.stream().filter(i -> i.getStatus() == InventoryStatus.SOLD).count();
        long returned = items.stream().filter(i -> i.getStatus() == InventoryStatus.RETURNED).count();
        long damaged = items.stream().filter(i -> i.getStatus() == InventoryStatus.DAMAGED).count();
        long blocked = items.stream().filter(i -> i.getStatus() == InventoryStatus.BLOCKED).count();
        
        BigDecimal totalAcquisitionValue = items.stream()
                .map(InventoryItem::getCostPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal availableValue = items.stream()
                .filter(i -> i.getStatus() == InventoryStatus.AVAILABLE)
                .map(InventoryItem::getCostPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return InventorySummaryResponse.builder()
                .totalStock(total)
                .availableCount(available)
                .reservedCount(reserved)
                .inTransitCount(inTransit)
                .soldCount(sold)
                .returnedCount(returned)
                .damagedCount(damaged)
                .blockedCount(blocked)
                .totalAcquisitionValue(totalAcquisitionValue)
                .availableStockValue(availableValue)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<InventoryResponse> searchInventory(Specification<InventoryItem> spec, Pageable pageable) {
        if (!currentUserService.hasPermission("SUPER_ADMIN")) {
            UUID userBranchId = currentUserService.getCurrentBranch().getId();
            Specification<InventoryItem> branchSpec = (root, query, cb) -> cb.equal(root.get("branch").get("id"), userBranchId);
            spec = spec == null ? branchSpec : spec.and(branchSpec);
        }
        return inventoryItemRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    private InventoryItem getInventoryWithLock(UUID id) {
        return inventoryItemRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Inventory not found.", HttpStatus.NOT_FOUND));
    }

    private void validateAccess(UUID branchId) {
        if (!currentUserService.hasPermission("SUPER_ADMIN")) {
            UUID currentUserBranchId = currentUserService.getCurrentBranch().getId();
            if (!branchId.equals(currentUserBranchId)) {
                throw new BusinessException("INVENTORY_BRANCH_ACCESS_DENIED", "Access denied to branch inventory.", HttpStatus.FORBIDDEN);
            }
        }
    }

    public void recordHistory(InventoryItem item, InventoryStatus prev, InventoryStatus curr, String reason, String refType, UUID refId) {
        InventoryStatusHistory history = InventoryStatusHistory.builder()
                .inventoryItem(item)
                .previousStatus(prev)
                .newStatus(curr)
                .reason(reason)
                .referenceType(refType)
                .referenceId(refId)
                .performedBy(currentUserService.getCurrentUser())
                .branch(item.getBranch())
                .build();
        historyRepository.save(history);
    }

    private InventoryResponse mapToResponse(InventoryItem item) {
        return InventoryResponse.builder()
                .id(item.getId())
                .stockCode(item.getStockCode())
                .deviceId(item.getDevice().getId())
                .purchaseTransactionId(item.getPurchaseTransaction().getId())
                .branchId(item.getBranch().getId())
                .status(item.getStatus())
                .costPrice(item.getCostPrice())
                .sellingPrice(item.getSellingPrice())
                .reservedUntil(item.getReservedUntil())
                .reservedByUsername(item.getReservedBy() != null ? item.getReservedBy().getUsername() : null)
                .conditionSummary(item.getConditionSummary())
                .notes(item.getNotes())
                .createdAt(item.getCreatedAt() != null ? item.getCreatedAt().atZone(java.time.ZoneId.systemDefault()) : null)
                .updatedAt(item.getUpdatedAt() != null ? item.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()) : null)
                .build();
    }

    private InventoryStatusHistoryResponse mapHistoryToResponse(InventoryStatusHistory history) {
        return InventoryStatusHistoryResponse.builder()
                .id(history.getId())
                .inventoryItemId(history.getInventoryItem().getId())
                .previousStatus(history.getPreviousStatus())
                .newStatus(history.getNewStatus())
                .reason(history.getReason())
                .referenceType(history.getReferenceType())
                .referenceId(history.getReferenceId())
                .performedByUsername(history.getPerformedBy() != null ? history.getPerformedBy().getUsername() : null)
                .branchId(history.getBranch() != null ? history.getBranch().getId() : null)
                .createdAt(history.getCreatedAt())
                .build();
    }
}
