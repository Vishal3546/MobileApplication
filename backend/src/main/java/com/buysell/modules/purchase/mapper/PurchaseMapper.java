package com.buysell.modules.purchase.mapper;

import com.buysell.modules.purchase.dto.PurchasePaymentResponse;
import com.buysell.modules.purchase.dto.PurchaseResponse;
import com.buysell.modules.purchase.entity.PurchasePayment;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import org.springframework.stereotype.Component;

@Component
public class PurchaseMapper {

    public PurchaseResponse toResponse(PurchaseTransaction entity) {
        if (entity == null) return null;
        
        return PurchaseResponse.builder()
                .id(entity.getId())
                .purchaseNumber(entity.getPurchaseNumber())
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getId() : null)
                .deviceId(entity.getDevice() != null ? entity.getDevice().getId() : null)
                .employeeId(entity.getEmployee() != null ? entity.getEmployee().getId() : null)
                .branchId(entity.getBranch() != null ? entity.getBranch().getId() : null)
                .suggestedPrice(entity.getSuggestedPrice())
                .negotiatedPrice(entity.getNegotiatedPrice())
                .finalPrice(entity.getFinalPrice())
                .notes(entity.getNotes())
                .transactionStatus(entity.getTransactionStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public PurchasePaymentResponse toPaymentResponse(PurchasePayment entity) {
        if (entity == null) return null;
        
        return PurchasePaymentResponse.builder()
                .id(entity.getId())
                .purchaseTransactionId(entity.getPurchaseTransaction() != null ? entity.getPurchaseTransaction().getId() : null)
                .paymentMode(entity.getPaymentMode())
                .amount(entity.getAmount())
                .referenceNumber(entity.getReferenceNumber())
                .paymentStatus(entity.getPaymentStatus())
                .transactionTime(entity.getTransactionTime())
                .idempotencyKey(entity.getIdempotencyKey())
                .processedBy(entity.getProcessedBy() != null ? entity.getProcessedBy().getId() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
