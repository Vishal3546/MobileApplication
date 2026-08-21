package com.buysell.modules.device.service.provider;

import com.buysell.exception.BusinessException;
import com.buysell.modules.device.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class LocalImeiVerificationProviderTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private LocalImeiVerificationProvider provider;

    private String validImei;

    @BeforeEach
    void setUp() {
        validImei = "356938035643809";
    }

    @Test
    void verifyImei_duplicate_throwsException() {
        doNothing().when(deviceRepository).acquireImeiLock(validImei);
        when(deviceRepository.existsByImeiCrossField(validImei)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            provider.verifyImei(validImei, "IMEI", null)
        );
        assertEquals("DEVICE_IMEI_ALREADY_EXISTS", ex.getCode());
    }

    @Test
    void verifyImei_duplicateWithExcludeId_throwsException() {
        UUID excludeId = UUID.randomUUID();
        doNothing().when(deviceRepository).acquireImeiLock(validImei);
        when(deviceRepository.existsByImeiCrossFieldExcludeId(validImei, excludeId)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> 
            provider.verifyImei(validImei, "IMEI", excludeId)
        );
        assertEquals("DEVICE_IMEI_ALREADY_EXISTS", ex.getCode());
    }
}
