package com.buysell.modules.sales.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.sales.dto.CreateSalePaymentRequest;
import com.buysell.modules.sales.dto.SalePaymentResponse;
import com.buysell.modules.sales.entity.SalePayment;
import com.buysell.modules.sales.entity.SaleTransaction;
import com.buysell.modules.sales.enums.PaymentMode;
import com.buysell.modules.sales.enums.PaymentStatus;
import com.buysell.modules.sales.enums.SaleStatus;
import com.buysell.modules.sales.repository.SalePaymentRepository;
import com.buysell.modules.sales.repository.SaleTransactionRepository;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;

@ExtendWith(MockitoExtension.class)
public class SalePaymentServiceTest {

    @Mock
    private SalePaymentRepository salePaymentRepository;

    @Mock
    private SaleTransactionRepository saleTransactionRepository;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SalePaymentService salePaymentService;

    private SaleTransaction sale;
    private Branch branch;
    private User user;

    @BeforeEach
    public void setUp() {
        org.junit.jupiter.api.Assertions.assertNotNull(auditService);
        branch = Branch.builder().id(UUID.randomUUID()).build();
        user = User.builder().id(UUID.randomUUID()).build();
        sale = SaleTransaction.builder()
                .id(UUID.randomUUID())
                .branch(branch)
                .saleStatus(SaleStatus.RESERVED)
                .finalAmount(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void createPayment_Success() {
        CreateSalePaymentRequest request = new CreateSalePaymentRequest();
        request.setSaleTransactionId(sale.getId());
        request.setAmount(new BigDecimal("100.00"));
        request.setPaymentMode(PaymentMode.CARD);
        request.setIdempotencyKey("key123");

        when(currentUserService.hasPermission("CREATE_SALE_PAYMENT")).thenReturn(true);
        when(salePaymentRepository.findByIdempotencyKey("key123")).thenReturn(Optional.empty());
        when(saleTransactionRepository.findByIdWithLock(sale.getId())).thenReturn(Optional.of(sale));
        when(currentUserService.isSuperAdmin()).thenReturn(true);
        when(salePaymentRepository.findBySaleTransactionIdAndPaymentStatus(sale.getId(), PaymentStatus.SUCCESS))
                .thenReturn(List.of());
        
        SalePayment savedPayment = SalePayment.builder()
                .id(UUID.randomUUID())
                .saleTransaction(sale)
                .amount(new BigDecimal("100.00"))
                .paymentMode(PaymentMode.CARD)
                .processedBy(user)
                .build();
        when(salePaymentRepository.save(any())).thenReturn(savedPayment);
        when(currentUserService.getCurrentUser()).thenReturn(user);

        SalePaymentResponse res = salePaymentService.createPayment(request);
        assertEquals(new BigDecimal("100.00"), res.getAmount());
    }

    @Test
    void createPayment_Idempotent_ReturnsExisting() {
        CreateSalePaymentRequest request = new CreateSalePaymentRequest();
        request.setIdempotencyKey("key123");

        when(currentUserService.hasPermission("CREATE_SALE_PAYMENT")).thenReturn(true);
        
        SalePayment existing = SalePayment.builder()
                .id(UUID.randomUUID())
                .saleTransaction(sale)
                .amount(new BigDecimal("50.00"))
                .processedBy(user)
                .build();
        when(salePaymentRepository.findByIdempotencyKey("key123")).thenReturn(Optional.of(existing));

        SalePaymentResponse res = salePaymentService.createPayment(request);
        assertEquals(new BigDecimal("50.00"), res.getAmount());
    }
}
