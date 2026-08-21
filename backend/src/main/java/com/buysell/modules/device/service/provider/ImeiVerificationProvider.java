package com.buysell.modules.device.service.provider;

public interface ImeiVerificationProvider {
    /**
     * Verifies the given IMEI format and checksum, and optionally checks for duplicates.
     * 
     * @param imei the IMEI to verify
     * @param fieldName the name of the field for error messages
     * @param excludeId an optional device ID to exclude from duplicate checks (for updates)
     * @return the normalized IMEI string
     */
    String verifyImei(String imei, String fieldName, java.util.UUID excludeId);
    
    default String verifyImei(String imei, String fieldName) {
        return verifyImei(imei, fieldName, null);
    }
}
