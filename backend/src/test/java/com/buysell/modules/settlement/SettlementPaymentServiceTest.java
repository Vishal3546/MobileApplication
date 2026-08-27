package com.buysell.modules.settlement;

import com.buysell.exception.BusinessException;

import com.buysell.modules.settlement.entity.SettlementPayment;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.enums.SettlementPaymentMode;
import com.buysell.modules.settlement.enums.SettlementStatus;
import com.buysell.modules.settlement.repository.SettlementPaymentRepository;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.modules.settlement.service.SettlementPaymentService;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettlementPaymentServiceTest {

    @Mock
    private ShopSettlementRepository settlementRepository;

    @Mock
    private SettlementPaymentRepository paymentRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SettlementPaymentService paymentService;

    private ShopSettlement settlement;
    private Shop sourceShop;
    private Shop destinationShop;

    @BeforeEach
    public void setup() {
        sourceShop = new Shop();
        sourceShop.setId(UUID.randomUUID());

        destinationShop = new Shop();
        destinationShop.setId(UUID.randomUUID());

        settlement = new ShopSettlement();
        settlement.setId(UUID.randomUUID());
        settlement.setSourceShop(sourceShop);
        settlement.setDestinationShop(destinationShop);
        settlement.setGrossAmount(BigDecimal.valueOf(1000));
        settlement.setRemainingAmount(BigDecimal.valueOf(1000));
        settlement.setPaidAmount(BigDecimal.ZERO);
        settlement.setStatus(SettlementStatus.PENDING);
    }

    @Test
    void createPayment_Success() {
        when(paymentRepository.findBySettlementIdAndIdempotencyKey(any(), any())).thenReturn(Optional.empty());
        when(settlementRepository.findByIdWithLock(settlement.getId())).thenReturn(Optional.of(settlement));
        when(currentUserService.isSuperAdmin()).thenReturn(true);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        SettlementPayment payment = paymentService.createPayment(
                settlement.getId(), BigDecimal.valueOf(200), SettlementPaymentMode.BANK_TRANSFER, "REF123", "KEY123");

        assertEquals(BigDecimal.valueOf(200), payment.getAmount());
        assertEquals(BigDecimal.valueOf(200), settlement.getPaidAmount());
        assertEquals(BigDecimal.valueOf(800), settlement.getRemainingAmount());
        assertEquals(SettlementStatus.PARTIALLY_PAID, settlement.getStatus());
        
        verify(settlementRepository).save(settlement);
    }

    @Test
    void createPayment_ExceedsRemaining_ThrowsException() {
        when(paymentRepository.findBySettlementIdAndIdempotencyKey(any(), any())).thenReturn(Optional.empty());
        when(settlementRepository.findByIdWithLock(settlement.getId())).thenReturn(Optional.of(settlement));
        when(currentUserService.isSuperAdmin()).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            paymentService.createPayment(settlement.getId(), BigDecimal.valueOf(1001), SettlementPaymentMode.BANK_TRANSFER, "REF123", "KEY123")
        );
        assertNotNull(ex);
    }

    @Test
    void createPayment_ReturnsExistingOnDuplicateIdempotency() {
        SettlementPayment existing = new SettlementPayment();
        existing.setId(UUID.randomUUID());
        
        when(paymentRepository.findBySettlementIdAndIdempotencyKey(settlement.getId(), "KEY123")).thenReturn(Optional.of(existing));

        SettlementPayment payment = paymentService.createPayment(
                settlement.getId(), BigDecimal.valueOf(200), SettlementPaymentMode.BANK_TRANSFER, "REF123", "KEY123");

        assertEquals(existing, payment);
        verify(settlementRepository, never()).findByIdWithLock(any());
    }
}
