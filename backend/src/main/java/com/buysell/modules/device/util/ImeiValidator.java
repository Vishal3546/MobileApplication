package com.buysell.modules.device.util;

import com.buysell.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ImeiValidator {

    public static String normalizeAndValidate(String imei, String fieldName) {
        if (imei == null) {
            return null;
        }

        // Normalize: Trim whitespace
        String normalized = imei.trim();

        if (normalized.isEmpty()) {
            return null; // Or throw exception if mandatory, but we handle mandatory at the request validation level
        }

        // Validate numeric characters and 15-digit format
        if (!normalized.matches("^\\d{15}$")) {
            throw new BusinessException("INVALID_IMEI", fieldName + " must be exactly 15 numeric digits.", HttpStatus.BAD_REQUEST);
        }

        // Validate Luhn checksum
        if (!isValidLuhn(normalized)) {
            throw new BusinessException("INVALID_IMEI_CHECKSUM", fieldName + " failed Luhn checksum validation.", HttpStatus.BAD_REQUEST);
        }

        return normalized;
    }

    private static boolean isValidLuhn(String imei) {
        int sum = 0;
        boolean alternate = false;
        
        // Loop from right to left
        for (int i = imei.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(imei.substring(i, i + 1));
            
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            
            sum += n;
            alternate = !alternate;
        }
        
        return (sum % 10 == 0);
    }
}
