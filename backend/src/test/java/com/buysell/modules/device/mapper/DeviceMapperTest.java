package com.buysell.modules.device.mapper;

import com.buysell.modules.device.dto.DeviceResponse;
import com.buysell.modules.device.dto.DeviceSummaryResponse;
import com.buysell.modules.device.entity.Device;
import com.buysell.security.CurrentUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceMapperTest {

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private DeviceMapper deviceMapper;

    private Device device;

    @BeforeEach
    void setUp() {
        device = new Device();
        device.setId(UUID.randomUUID());
        device.setImei1("123456789012345");
        device.setImei2("987654321098765");
        device.setBrand("Apple");
        device.setModel("iPhone 13");
    }

    @Test
    void toResponse_withPermission_shouldNotMask() {
        when(currentUserService.hasPermission("VIEW_FULL_DEVICE_IMEI")).thenReturn(true);

        DeviceResponse response = deviceMapper.toResponse(device);

        assertEquals("123456789012345", response.getImei1());
        assertEquals("987654321098765", response.getImei2());
    }

    @Test
    void toResponse_withoutPermission_shouldMask() {
        when(currentUserService.hasPermission("VIEW_FULL_DEVICE_IMEI")).thenReturn(false);

        DeviceResponse response = deviceMapper.toResponse(device);

        assertEquals("***********2345", response.getImei1());
        assertEquals("***********8765", response.getImei2());
    }

    @Test
    void toSummaryResponse_withoutPermission_shouldMask() {
        when(currentUserService.hasPermission("VIEW_FULL_DEVICE_IMEI")).thenReturn(false);

        DeviceSummaryResponse response = deviceMapper.toSummaryResponse(device);

        assertEquals("***********2345", response.getImei1());
    }

    @Test
    void testMaskingShortImei() {
        // Edge cases
        device.setImei1("1234");
        when(currentUserService.hasPermission("VIEW_FULL_DEVICE_IMEI")).thenReturn(false);

        DeviceResponse response = deviceMapper.toResponse(device);

        assertEquals("****", response.getImei1());
    }
}
