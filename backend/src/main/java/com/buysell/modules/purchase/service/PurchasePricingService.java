package com.buysell.modules.purchase.service;

import com.buysell.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PurchasePricingService {

    public void validatePrices(BigDecimal suggested, BigDecimal negotiated, BigDecimal finalPrice) {
        if (suggested != null && suggested.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE", "Suggested price cannot be negative", HttpStatus.BAD_REQUEST);
        }
        if (negotiated != null && negotiated.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE", "Negotiated price cannot be negative", HttpStatus.BAD_REQUEST);
        }
        if (finalPrice != null && finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("INVALID_PRICE", "Final price cannot be negative", HttpStatus.BAD_REQUEST);
        }
    }
}
