package com.buysell.modules.sales.service;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.buysell.exception.BusinessException;
import com.buysell.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
public class SalePricingServiceTest {

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private SalePricingService salePricingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(salePricingService, "maxDiscountPercentage", new BigDecimal("50"));
        ReflectionTestUtils.setField(salePricingService, "maxAbsoluteDiscount", new BigDecimal("5000"));
    }

    @Test
    void calculateFinalAmount_Success() {
        BigDecimal finalAmt = salePricingService.calculateFinalAmount(new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("5"));
        assertEquals(new BigDecimal("95"), finalAmt);
    }

    @Test
    void calculateFinalAmount_NegativePrice_ThrowsException() {
        assertThrows(BusinessException.class, () -> 
            salePricingService.calculateFinalAmount(new BigDecimal("-10"), BigDecimal.ZERO, BigDecimal.ZERO)
        );
    }

    @Test
    void validateDiscount_Success() {
        when(currentUserService.hasPermission("APPLY_SALE_DISCOUNT")).thenReturn(true);
        salePricingService.validateDiscount(new BigDecimal("1000"), new BigDecimal("100")); // 10% discount
    }

    @Test
    void validateDiscount_ExceedsAbsolute_ThrowsException() {
        when(currentUserService.hasPermission("APPLY_SALE_DISCOUNT")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            salePricingService.validateDiscount(new BigDecimal("20000"), new BigDecimal("6000"))
        );
        assertEquals("SALE_DISCOUNT_LIMIT_EXCEEDED", ex.getCode());
    }

    @Test
    void validateDiscount_ExceedsPercentage_ThrowsException() {
        when(currentUserService.hasPermission("APPLY_SALE_DISCOUNT")).thenReturn(true);
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            salePricingService.validateDiscount(new BigDecimal("1000"), new BigDecimal("600")) // 60%
        );
        assertEquals("SALE_DISCOUNT_LIMIT_EXCEEDED", ex.getCode());
    }

    @Test
    void validateDiscount_NoPermission_ThrowsException() {
        when(currentUserService.hasPermission("APPLY_SALE_DISCOUNT")).thenReturn(false);
        assertThrows(BusinessException.class, () -> 
            salePricingService.validateDiscount(new BigDecimal("1000"), new BigDecimal("100"))
        );
    }
}
