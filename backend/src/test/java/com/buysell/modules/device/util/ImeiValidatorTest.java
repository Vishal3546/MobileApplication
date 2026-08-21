package com.buysell.modules.device.util;

import com.buysell.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImeiValidatorTest {

    @Test
    void testValidImei() {
        // Known valid IMEI for testing Luhn (randomly valid 15-digit)
        // 356938035643809 is given as an example in user prompt. Let's test it:
        // Wait, the prompt example was 356938035643809. Let's see if it's valid.
        String validImei = "356938035643809";
        
        String result = ImeiValidator.normalizeAndValidate(validImei, "IMEI");
        assertEquals(validImei, result);
    }

    @Test
    void testNormalizeWhitespace() {
        String input = "  356938035643809  ";
        String result = ImeiValidator.normalizeAndValidate(input, "IMEI");
        assertEquals("356938035643809", result);
    }

    @Test
    void testNullImei() {
        assertNull(ImeiValidator.normalizeAndValidate(null, "IMEI"));
    }
    
    @Test
    void testEmptyImei() {
        assertNull(ImeiValidator.normalizeAndValidate("   ", "IMEI"));
    }

    @Test
    void testInvalidLength() {
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            ImeiValidator.normalizeAndValidate("12345678901234", "IMEI")
        );
        assertEquals("INVALID_IMEI", ex.getCode());
    }

    @Test
    void testNonNumeric() {
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            ImeiValidator.normalizeAndValidate("356938A35643809", "IMEI")
        );
        assertEquals("INVALID_IMEI", ex.getCode());
    }

    @Test
    void testInvalidLuhnChecksum() {
        // Change one digit of the valid IMEI
        String invalidLuhn = "356938035643808"; 
        BusinessException ex = assertThrows(BusinessException.class, () -> 
            ImeiValidator.normalizeAndValidate(invalidLuhn, "IMEI")
        );
        assertEquals("INVALID_IMEI_CHECKSUM", ex.getCode());
    }
}
