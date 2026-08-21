package com.buysell.modules.reports.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

public class ReportDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseReportResponse {
        private long purchaseCount;
        private BigDecimal totalPurchaseValue;
        private BigDecimal averagePurchasePrice;
        private long completedPurchases;
        private long cancelledPurchases;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseReportDetail {
        private UUID id;
        private String transactionNumber;
        private ZonedDateTime createdAt;
        private String deviceBrand;
        private String deviceModel;
        private String status;
        private BigDecimal finalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryReportResponse {
        private long totalInventory;
        private long available;
        private long reserved;
        private long inTransit;
        private long sold;
        private long returned;
        private long damaged;
        private long blocked;
        private BigDecimal totalAcquisitionValue;
        private BigDecimal availableStockValue;
        private BigDecimal totalListedSellingValue;
        private BigDecimal unrealizedStockMargin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryReportDetail {
        private UUID id;
        private String stockCode;
        private ZonedDateTime createdAt;
        private String deviceBrand;
        private String deviceModel;
        private String status;
        private BigDecimal costPrice;
        private BigDecimal sellingPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfitReportResponse {
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal grossProfit;
        private BigDecimal profitMarginPercentage; // (grossProfit / revenue) * 100
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendReportResponse {
        private LocalDate date;
        private long purchaseCount;
        private BigDecimal purchaseValue;
        private long saleCount;
        private BigDecimal saleValue;
        private BigDecimal grossProfit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandPerformanceResponse {
        private String brand;
        private long purchasedCount;
        private BigDecimal purchasedValue;
        private long soldCount;
        private BigDecimal soldValue;
        private BigDecimal grossProfit;
        private BigDecimal averageSaleValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelPerformanceResponse {
        private String brand;
        private String model;
        private long soldCount;
        private BigDecimal soldValue;
        private BigDecimal averageSaleValue;
        private BigDecimal grossProfit;
        private Double averageDaysInStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockAgeingResponse {
        private String bucket; // e.g. "0-7 days"
        private long stockCount;
        private BigDecimal stockCostValue;
        private BigDecimal stockSellingValue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BranchPerformanceResponse {
        private String branch;
        private long purchaseCount;
        private BigDecimal purchaseValue;
        private long salesCount;
        private BigDecimal salesValue;
        private BigDecimal grossProfit;
        private long availableStock;
        private long reservedStock;
        private long inTransitStock;
        private long soldStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeePerformanceResponse {
        private String employee;
        private long purchaseCount;
        private BigDecimal purchaseValue;
        private long salesCount;
        private BigDecimal salesValue;
        private BigDecimal grossProfit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentReportResponse {
        private String type; // purchase payments, sale payments
        private BigDecimal cash;
        private BigDecimal upi;
        private BigDecimal bankTransfer;
        private BigDecimal card;
        private BigDecimal other;
        private BigDecimal successfulAmount;
        private BigDecimal failedAmount;
        private BigDecimal pendingAmount;
        private long transactionCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExceptionReportResponse {
        private long pendingKyc;
        private long rejectedKyc;
        private long unverifiedDevices;
        private long failedInspections;
        private long pendingPurchases;
        private long pendingPurchasePayments;
        private long reservedStockNearingExpiry;
        private long pendingSales;
        private long failedPayments;
        private long pendingStockTransfers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopDeviceResponse {
        private String brand;
        private String model;
        private long count; // sold_count or purchase_count
        private BigDecimal value; // sold_value or purchase_value
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleReportDetail {
        private UUID id;
        private String saleNumber;
        private ZonedDateTime createdAt;
        private String deviceBrand;
        private String deviceModel;
        private String saleStatus;
        private BigDecimal sellingPrice;
        private BigDecimal finalAmount;
        private BigDecimal grossProfit;
    }
}
