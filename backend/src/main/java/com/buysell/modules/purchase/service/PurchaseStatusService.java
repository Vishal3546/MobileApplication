package com.buysell.modules.purchase.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.purchase.enums.TransactionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseStatusService {

    public void validateTransition(TransactionStatus currentStatus, TransactionStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new BusinessException("PURCHASE_INVALID_STATE", "Purchase is already in status: " + currentStatus, HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == TransactionStatus.COMPLETED) {
            throw new BusinessException("PURCHASE_ALREADY_COMPLETED", "Cannot transition a completed purchase.", HttpStatus.BAD_REQUEST);
        }

        if (currentStatus == TransactionStatus.CANCELLED) {
            throw new BusinessException("PURCHASE_ALREADY_CANCELLED", "Cannot transition a cancelled purchase.", HttpStatus.BAD_REQUEST);
        }

        // Allow cancellation from any non-terminal state
        if (newStatus == TransactionStatus.CANCELLED) {
            return;
        }

        boolean isValid = switch (currentStatus) {
            case INITIATED -> newStatus == TransactionStatus.PENDING_KYC;
            case PENDING_KYC -> newStatus == TransactionStatus.PENDING_DEVICE_VERIFICATION;
            case PENDING_DEVICE_VERIFICATION -> newStatus == TransactionStatus.PENDING_INSPECTION;
            case PENDING_INSPECTION -> newStatus == TransactionStatus.PENDING_CONSENT;
            case PENDING_CONSENT -> newStatus == TransactionStatus.PENDING_PAYMENT;
            case PENDING_PAYMENT -> newStatus == TransactionStatus.COMPLETED;
            default -> false;
        };

        if (!isValid) {
            throw new BusinessException("PURCHASE_INVALID_STATE", 
                "Invalid status transition from " + currentStatus + " to " + newStatus, 
                HttpStatus.BAD_REQUEST);
        }
    }
    
    public List<TransactionStatus> getTerminalStatuses() {
        return List.of(TransactionStatus.COMPLETED, TransactionStatus.CANCELLED);
    }
}
