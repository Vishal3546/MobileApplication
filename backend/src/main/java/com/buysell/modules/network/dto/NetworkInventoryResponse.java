package com.buysell.modules.network.dto;

import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class NetworkInventoryResponse {
    private UUID id;
    private String stockCode;
    private String brand;
    private String model;
    private String maskedImei;
    private String storageCapacity;
    private String color;
    private String conditionSummary;
    private BigDecimal sellingPrice;
    private InventoryStatus status;
    private UUID shopId;
    private String shopName;
    private UUID branchId;
    private String branchName;
    
    public static NetworkInventoryResponse fromEntity(InventoryItem item) {
        String imei = item.getDevice().getImei1();
        String masked = (imei != null && imei.length() > 4) ? "*******" + imei.substring(imei.length() - 4) : "*******";
        
        return NetworkInventoryResponse.builder()
                .id(item.getId())
                .stockCode(item.getStockCode())
                .brand(item.getDevice().getBrand())
                .model(item.getDevice().getModel())
                .maskedImei(masked)
                .storageCapacity(item.getDevice().getStorageGb() != null ? item.getDevice().getStorageGb() + " GB" : null)
                .color(item.getDevice().getColor())
                .conditionSummary(item.getConditionSummary())
                .sellingPrice(item.getSellingPrice())
                .status(item.getStatus())
                .shopId(item.getBranch().getShop().getId())
                .shopName(item.getBranch().getShop().getName())
                .branchId(item.getBranch().getId())
                .branchName(item.getBranch().getName())
                .build();
    }
}
