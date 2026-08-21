package com.buysell.modules.audit.service;

import java.util.UUID;

public interface AuditService {
    void logAction(UUID userId, UUID branchId, String actionType, String entityName, UUID entityId, String oldValue, String newValue, String ipAddress, String userAgent);
}
