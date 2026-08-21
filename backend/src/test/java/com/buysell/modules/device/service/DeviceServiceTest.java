package com.buysell.modules.device.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.device.dto.CreateDeviceRequest;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.enums.DeviceStatus;
import com.buysell.modules.device.repository.DeviceRepository;
import com.buysell.modules.device.service.provider.ImeiVerificationProvider;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceLifecycleService lifecycleService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private ImeiVerificationProvider imeiVerificationProvider;

    @InjectMocks
    private DeviceService deviceService;

    private User mockUser;
    private Device mockDevice;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(UUID.randomUUID());

        mockDevice = new Device();
        mockDevice.setId(UUID.randomUUID());
        mockDevice.setImei1("356938035643809");
        mockDevice.setBrand("Apple");
        mockDevice.setModel("iPhone 13");
        mockDevice.setStatus(DeviceStatus.ACTIVE);
    }

    @Test
    void createDevice_success() {
        CreateDeviceRequest req = new CreateDeviceRequest();
        req.setImei1("356938035643809");
        req.setBrand("Apple");
        req.setModel("iPhone 13");

        when(imeiVerificationProvider.verifyImei(req.getImei1(), "IMEI1")).thenReturn(req.getImei1());
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        when(deviceRepository.save(any(Device.class))).thenReturn(mockDevice);

        Device created = deviceService.createDevice(req);

        assertNotNull(created);
        assertEquals("356938035643809", created.getImei1());
        verify(lifecycleService).recordEvent(eq(created), any(), any(), any());
    }

    @Test
    void createDevice_sameImei1AndImei2_throwsException() {
        CreateDeviceRequest req = new CreateDeviceRequest();
        req.setImei1("356938035643809");
        req.setImei2("356938035643809");

        when(imeiVerificationProvider.verifyImei(req.getImei1(), "IMEI1")).thenReturn(req.getImei1());
        when(imeiVerificationProvider.verifyImei(req.getImei2(), "IMEI2")).thenReturn(req.getImei2());

        BusinessException ex = assertThrows(BusinessException.class, () -> deviceService.createDevice(req));
        assertEquals("INVALID_IMEI", ex.getCode());
        verify(deviceRepository, never()).save(any());
    }

    @Test
    void updateDeviceStatus_toBlocked_success() {
        when(deviceRepository.findById(mockDevice.getId())).thenReturn(Optional.of(mockDevice));
        when(currentUserService.getCurrentUser()).thenReturn(mockUser);
        when(deviceRepository.save(any(Device.class))).thenReturn(mockDevice);

        deviceService.updateDeviceStatus(mockDevice.getId(), DeviceStatus.BLOCKED);

        assertEquals(DeviceStatus.BLOCKED, mockDevice.getStatus());
        verify(lifecycleService).recordEvent(eq(mockDevice), argThat(event -> event.name().equals("DEVICE_BLOCKED")), any(), any());
    }
}
