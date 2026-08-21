package com.buysell.modules.audit.service;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class NoOpAuditService implements AuditService {
    @Override
    public void logAction(UUID userId, UUID branchId, String actionType, String entityName, UUID entityId, String oldValue, String newValue, String ipAddress, String userAgent) {
        // No-op for now
    }
}
