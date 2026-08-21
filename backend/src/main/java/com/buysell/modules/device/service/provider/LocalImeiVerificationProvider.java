package com.buysell.modules.device.service.provider;

import com.buysell.exception.BusinessException;
import com.buysell.modules.device.repository.DeviceRepository;
import com.buysell.modules.device.util.ImeiValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalImeiVerificationProvider implements ImeiVerificationProvider {

    private final DeviceRepository deviceRepository;

    @Override
    public String verifyImei(String imei, String fieldName, UUID excludeId) {
        String normalizedImei = ImeiValidator.normalizeAndValidate(imei, fieldName);
        
        if (normalizedImei != null) {
            // Acquire transaction-level advisory lock on this IMEI to prevent concurrent insertion race conditions
            deviceRepository.acquireImeiLock(normalizedImei);
            
            boolean exists = (excludeId == null) ? 
                deviceRepository.existsByImeiCrossField(normalizedImei) : 
                deviceRepository.existsByImeiCrossFieldExcludeId(normalizedImei, excludeId);
                
            if (exists) {
                throw new BusinessException("DEVICE_IMEI_ALREADY_EXISTS", "Device with this IMEI already exists in the system.", HttpStatus.CONFLICT);
            }
        }
        
        return normalizedImei;
    }
}
