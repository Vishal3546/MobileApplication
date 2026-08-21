package com.buysell.modules.sales.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.buysell.exception.BusinessException;
import com.buysell.security.CurrentUserService;

@Service
public class SalePricingService {

    private final CurrentUserService currentUserService;
    
    @Value("${sale.discount.max-percentage:100}")
    private BigDecimal maxDiscountPercentage;
    
    @Value("${sale.discount.max-absolute:100000.00}")
    private BigDecimal maxAbsoluteDiscount;

    public SalePricingService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public BigDecimal calculateFinalAmount(BigDecimal sellingPrice, BigDecimal discountAmount, BigDecimal taxAmount) {
        if (sellingPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("SALE_PRICE_INVALID", "Selling price cannot be negative", HttpStatus.BAD_REQUEST);
        }
        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("SALE_PRICE_INVALID", "Discount amount cannot be negative", HttpStatus.BAD_REQUEST);
        }
        if (taxAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("SALE_PRICE_INVALID", "Tax amount cannot be negative", HttpStatus.BAD_REQUEST);
        }

        BigDecimal finalAmount = sellingPrice.subtract(discountAmount).add(taxAmount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("SALE_PRICE_INVALID", "Final amount cannot be negative", HttpStatus.BAD_REQUEST);
        }

        return finalAmount;
    }

    public void validateDiscount(BigDecimal sellingPrice, BigDecimal discountAmount) {
        if (discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }

        if (!currentUserService.hasPermission("APPLY_SALE_DISCOUNT")) {
            throw new BusinessException("SALE_DISCOUNT_LIMIT_EXCEEDED", "User does not have permission to apply discount", HttpStatus.FORBIDDEN);
        }

        if (discountAmount.compareTo(maxAbsoluteDiscount) > 0) {
            throw new BusinessException("SALE_DISCOUNT_LIMIT_EXCEEDED", "Discount exceeds maximum allowed absolute amount", HttpStatus.BAD_REQUEST);
        }

        BigDecimal percentage = discountAmount.divide(sellingPrice, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
        if (percentage.compareTo(maxDiscountPercentage) > 0) {
            throw new BusinessException("SALE_DISCOUNT_LIMIT_EXCEEDED", "Discount exceeds maximum allowed percentage", HttpStatus.BAD_REQUEST);
        }
    }

    public void validatePriceOverride() {
        if (!currentUserService.hasPermission("OVERRIDE_SALE_PRICE")) {
            throw new BusinessException("SALE_PRICE_OVERRIDE_NOT_ALLOWED", "User does not have permission to override selling price", HttpStatus.FORBIDDEN);
        }
    }
}
