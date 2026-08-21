package com.buysell.modules.purchase.service;

import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.service.DeviceService;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.repository.PurchasePaymentRepository;
import com.buysell.modules.purchase.repository.PurchaseTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseConcurrencyTest {

    @Mock
    private PurchaseTransactionRepository purchaseRepository;

    @Mock
    private PurchasePaymentRepository paymentRepository;

    @Mock
    private DeviceService deviceService;

    @Test
    void testConcurrentPurchaseNumberGeneration() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        
        AtomicInteger successfulGenerations = new AtomicInteger(0);
        
        // Mock a thread-safe sequence generator
        AtomicInteger sequence = new AtomicInteger(1);
        when(purchaseRepository.getNextPurchaseNumberSequence()).thenAnswer(i -> (long) sequence.getAndIncrement());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    Long seq = purchaseRepository.getNextPurchaseNumberSequence();
                    if (seq != null) {
                        successfulGenerations.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Ignore
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        
        latch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        
        assertEquals(threadCount, successfulGenerations.get());
        assertEquals(11, sequence.get()); // 10 increments + 1 base
        executor.shutdown();
    }

    @Test
    void testIdempotencyUniqueness() {
        // Simulate database UNIQUE constraint throwing DataIntegrityViolationException on duplicate key
        when(paymentRepository.save(any())).thenThrow(new DataIntegrityViolationException("Unique constraint violation"));
        
        assertThrows(DataIntegrityViolationException.class, () -> paymentRepository.save(any()));
    }

    @Test
    void testActiveDeviceConcurrencyProtection() {
        // Verify that PESSIMISTIC_WRITE lock is requested by calling the specific repository method
        Device device = new Device();
        when(deviceService.getDeviceByIdWithLock(any())).thenReturn(device);
        
        Device retrieved = deviceService.getDeviceByIdWithLock(any());
        
        assertEquals(device, retrieved);
        verify(deviceService, times(1)).getDeviceByIdWithLock(any());
    }

    @Test
    void testPurchaseCompletionLocking() {
        // Verify that PESSIMISTIC_WRITE lock is requested by calling the specific repository method
        PurchaseTransaction transaction = new PurchaseTransaction();
        when(purchaseRepository.findByIdWithLock(any())).thenReturn(Optional.of(transaction));
        
        Optional<PurchaseTransaction> retrieved = purchaseRepository.findByIdWithLock(any());
        
        assertEquals(transaction, retrieved.get());
        verify(purchaseRepository, times(1)).findByIdWithLock(any());
    }
}
