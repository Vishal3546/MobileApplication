package com.buysell.modules.inventory.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.branch.repository.BranchRepository;
import com.buysell.modules.inventory.dto.*;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.entity.StockTransfer;
import com.buysell.modules.inventory.entity.StockTransferItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.enums.TransferStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.inventory.repository.StockTransferItemRepository;
import com.buysell.modules.inventory.repository.StockTransferRepository;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockTransferService {

    private final StockTransferRepository transferRepository;
    private final StockTransferItemRepository transferItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryService inventoryService;
    private final BranchRepository branchRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public StockTransferResponse createTransfer(StockTransferRequest request) {
        Branch fromBranch = currentUserService.getCurrentBranch();
        
        if (fromBranch.getId().equals(request.getToBranchId())) {
            throw new BusinessException("STOCK_TRANSFER_INVALID", "Cannot transfer to the same branch.", HttpStatus.BAD_REQUEST);
        }

        Branch toBranch = branchRepository.findById(request.getToBranchId())
                .orElseThrow(() -> new BusinessException("BRANCH_NOT_FOUND", "Destination branch not found.", HttpStatus.NOT_FOUND));

        String transferNumber = generateTransferNumber();

        StockTransfer transfer = StockTransfer.builder()
                .transferNumber(transferNumber)
                .fromBranch(fromBranch)
                .toBranch(toBranch)
                .status(TransferStatus.DRAFT)
                .notes(request.getNotes())
                .createdBy(currentUserService.getCurrentUser())
                .build();

        for (UUID itemId : request.getInventoryItemIds()) {
            InventoryItem item = inventoryItemRepository.findByIdWithLock(itemId)
                    .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Item " + itemId + " not found.", HttpStatus.NOT_FOUND));

            if (!item.getBranch().getId().equals(fromBranch.getId())) {
                throw new BusinessException("INVENTORY_BRANCH_MISMATCH", "Item " + itemId + " does not belong to the source branch.", HttpStatus.BAD_REQUEST);
            }

            if (item.getStatus() != InventoryStatus.AVAILABLE) {
                throw new BusinessException("INVENTORY_NOT_AVAILABLE", "Item " + itemId + " is not AVAILABLE.", HttpStatus.BAD_REQUEST);
            }

            // Check if already in another active transfer
            if (transferItemRepository.existsByInventoryItemIdAndStockTransferStatusIn(
                    itemId, Arrays.asList(TransferStatus.DRAFT, TransferStatus.REQUESTED, TransferStatus.APPROVED, TransferStatus.IN_TRANSIT))) {
                throw new BusinessException("INVENTORY_TRANSFER_NOT_ALLOWED", "Item " + itemId + " is already in an active transfer.", HttpStatus.BAD_REQUEST);
            }

            StockTransferItem transferItem = StockTransferItem.builder()
                    .inventoryItem(item)
                    .build();
            transfer.addItem(transferItem);
        }

        transfer = transferRepository.save(transfer);

        auditService.logAction(currentUserService.getCurrentUserId(), fromBranch.getId(),
                "STOCK_TRANSFER_CREATED", "StockTransfer", transfer.getId(),
                null, null, null, "Stock transfer " + transferNumber + " created.");

        return mapToResponse(transfer);
    }

    @Transactional
    public StockTransferResponse transitionTransfer(UUID id, TransferStatus newStatus) {
        StockTransfer transfer = transferRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException("STOCK_TRANSFER_NOT_FOUND", "Transfer not found.", HttpStatus.NOT_FOUND));

        TransferStatus currentStatus = transfer.getStatus();

        // Validate allowed transitions
        if (!isTransitionAllowed(currentStatus, newStatus)) {
            throw new BusinessException("STOCK_TRANSFER_INVALID_STATE", "Invalid transition from " + currentStatus + " to " + newStatus, HttpStatus.BAD_REQUEST);
        }

        User currentUser = currentUserService.getCurrentUser();

        transfer.setStatus(newStatus);
        
        switch (newStatus) {
            case REQUESTED -> {
                transfer.setRequestedAt(ZonedDateTime.now());
                transfer.setRequestedBy(currentUser);
            }
            case APPROVED -> {
                // Need permission
                if (!currentUserService.hasPermission("APPROVE_STOCK_TRANSFER")) {
                    throw new BusinessException("ACCESS_DENIED", "You do not have permission to approve stock transfers.", HttpStatus.FORBIDDEN);
                }
                transfer.setApprovedAt(ZonedDateTime.now());
                transfer.setApprovedBy(currentUser);
            }
            case IN_TRANSIT -> {
                for (StockTransferItem transferItem : transfer.getItems()) {
                    InventoryItem item = inventoryItemRepository.findByIdWithLock(transferItem.getInventoryItem().getId())
                            .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Item not found.", HttpStatus.NOT_FOUND));

                    if (item.getStatus() != InventoryStatus.AVAILABLE) {
                        throw new BusinessException("INVENTORY_NOT_AVAILABLE", "Item is not available.", HttpStatus.BAD_REQUEST);
                    }
                    if (!item.getBranch().getId().equals(transfer.getFromBranch().getId())) {
                        throw new BusinessException("INVENTORY_BRANCH_MISMATCH", "Item no longer in source branch.", HttpStatus.BAD_REQUEST);
                    }
                    
                    item.setStatus(InventoryStatus.IN_TRANSIT);
                    item = inventoryItemRepository.save(item);
                    inventoryService.recordHistory(item, InventoryStatus.AVAILABLE, InventoryStatus.IN_TRANSIT,
                            "Transfer started", "STOCK_TRANSFER", transfer.getId());
                            
                    auditService.logAction(currentUserService.getCurrentUserId(), item.getBranch().getId(),
                            "INVENTORY_STATUS_CHANGED", "InventoryItem", item.getId(),
                            item.getPurchaseTransaction().getId().toString(), item.getDevice().getId().toString(),
                            null, "Status changed to IN_TRANSIT due to transfer.");
                }
            }
            case CANCELLED, REJECTED -> {
                if (currentStatus == TransferStatus.IN_TRANSIT) {
                    throw new BusinessException("STOCK_TRANSFER_INVALID_STATE", "Cannot cancel or reject an IN_TRANSIT transfer. Explicit reversal is required.", HttpStatus.BAD_REQUEST);
                }
            }
            default -> {
            }
        }

        transfer = transferRepository.save(transfer);

        auditService.logAction(currentUserService.getCurrentUserId(), transfer.getFromBranch().getId(),
                "STOCK_TRANSFER_" + newStatus.name(), "StockTransfer", transfer.getId(),
                null, null, null, "Stock transfer status changed to " + newStatus);

        return mapToResponse(transfer);
    }

    @Transactional
    public StockTransferResponse completeTransfer(UUID id) {
        StockTransfer transfer = transferRepository.findByIdWithLock(id)
                .orElseThrow(() -> new BusinessException("STOCK_TRANSFER_NOT_FOUND", "Transfer not found.", HttpStatus.NOT_FOUND));

        if (transfer.getStatus() != TransferStatus.IN_TRANSIT) {
            throw new BusinessException("STOCK_TRANSFER_INVALID_STATE", "Only IN_TRANSIT transfers can be completed.", HttpStatus.BAD_REQUEST);
        }

        if (!currentUserService.hasPermission("COMPLETE_STOCK_TRANSFER")) {
            throw new BusinessException("ACCESS_DENIED", "You do not have permission to complete stock transfers.", HttpStatus.FORBIDDEN);
        }

        // Verify user belongs to destination branch or is SUPER_ADMIN
        if (!currentUserService.hasPermission("SUPER_ADMIN") && 
            !currentUserService.getCurrentBranch().getId().equals(transfer.getToBranch().getId())) {
            throw new BusinessException("INVENTORY_BRANCH_ACCESS_DENIED", "Only destination branch can complete the transfer.", HttpStatus.FORBIDDEN);
        }

        transfer.setStatus(TransferStatus.COMPLETED);
        transfer.setCompletedAt(ZonedDateTime.now());

        // Process items
        for (StockTransferItem transferItem : transfer.getItems()) {
            InventoryItem item = inventoryItemRepository.findByIdWithLock(transferItem.getInventoryItem().getId())
                    .orElseThrow(() -> new BusinessException("INVENTORY_NOT_FOUND", "Item not found.", HttpStatus.NOT_FOUND));

            if (!item.getBranch().getId().equals(transfer.getFromBranch().getId())) {
                throw new BusinessException("INVENTORY_BRANCH_MISMATCH", "Item no longer in source branch.", HttpStatus.BAD_REQUEST);
            }

            if (item.getStatus() != InventoryStatus.IN_TRANSIT) {
                throw new BusinessException("INVENTORY_NOT_IN_TRANSIT", "Item is not IN_TRANSIT.", HttpStatus.BAD_REQUEST);
            }

            item.setBranch(transfer.getToBranch());
            item.setStatus(InventoryStatus.AVAILABLE);
            item = inventoryItemRepository.save(item);

            inventoryService.recordHistory(item, InventoryStatus.IN_TRANSIT, InventoryStatus.AVAILABLE,
                    "Branch transfer completed", "STOCK_TRANSFER", transfer.getId());
                    
            auditService.logAction(currentUserService.getCurrentUserId(), item.getBranch().getId(),
                    "INVENTORY_STATUS_CHANGED", "InventoryItem", item.getId(),
                    item.getPurchaseTransaction().getId().toString(), item.getDevice().getId().toString(),
                    null, "Status changed to AVAILABLE and branch updated due to transfer completion.");
        }

        transfer = transferRepository.save(transfer);

        auditService.logAction(currentUserService.getCurrentUserId(), transfer.getToBranch().getId(),
                "STOCK_TRANSFER_COMPLETED", "StockTransfer", transfer.getId(),
                null, null, null, "Stock transfer completed.");

        return mapToResponse(transfer);
    }

    private boolean isTransitionAllowed(TransferStatus current, TransferStatus next) {
        return switch (current) {
            case DRAFT -> next == TransferStatus.REQUESTED || next == TransferStatus.CANCELLED;
            case REQUESTED -> next == TransferStatus.APPROVED || next == TransferStatus.REJECTED || next == TransferStatus.CANCELLED;
            case APPROVED -> next == TransferStatus.IN_TRANSIT;
            case IN_TRANSIT -> next == TransferStatus.COMPLETED;
            default -> false;
        };
    }

    private String generateTransferNumber() {
        Long sequence = transferRepository.getNextTransferNumberSequence();
        return String.format("TRF-%d-%06d", Year.now().getValue(), sequence);
    }

    @Transactional(readOnly = true)
    public StockTransferResponse getTransferById(UUID id) {
        StockTransfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new BusinessException("STOCK_TRANSFER_NOT_FOUND", "Transfer not found.", HttpStatus.NOT_FOUND));
        
        // Ensure access to either from or to branch
        UUID userBranch = currentUserService.getCurrentBranch().getId();
        if (!currentUserService.hasPermission("SUPER_ADMIN") &&
            !transfer.getFromBranch().getId().equals(userBranch) &&
            !transfer.getToBranch().getId().equals(userBranch)) {
            throw new BusinessException("INVENTORY_BRANCH_ACCESS_DENIED", "Access denied.", HttpStatus.FORBIDDEN);
        }
        
        return mapToResponse(transfer);
    }

    private StockTransferResponse mapToResponse(StockTransfer transfer) {
        return StockTransferResponse.builder()
                .id(transfer.getId())
                .transferNumber(transfer.getTransferNumber())
                .fromBranchId(transfer.getFromBranch().getId())
                .toBranchId(transfer.getToBranch().getId())
                .status(transfer.getStatus())
                .requestedByUsername(transfer.getRequestedBy() != null ? transfer.getRequestedBy().getUsername() : null)
                .approvedByUsername(transfer.getApprovedBy() != null ? transfer.getApprovedBy().getUsername() : null)
                .requestedAt(transfer.getRequestedAt())
                .approvedAt(transfer.getApprovedAt())
                .completedAt(transfer.getCompletedAt())
                .notes(transfer.getNotes())
                .createdAt(transfer.getCreatedAt() != null ? transfer.getCreatedAt().atZone(java.time.ZoneId.systemDefault()) : null)
                .updatedAt(transfer.getUpdatedAt() != null ? transfer.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()) : null)
                .items(transfer.getItems().stream().map(i -> StockTransferItemResponse.builder()
                        .id(i.getId())
                        .inventoryItemId(i.getInventoryItem().getId())
                        .stockCode(i.getInventoryItem().getStockCode())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
