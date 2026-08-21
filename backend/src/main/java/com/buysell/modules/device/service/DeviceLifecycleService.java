package com.buysell.modules.device.service;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.entity.DeviceLifecycleHistory;
import com.buysell.modules.device.enums.LifecycleEventType;
import com.buysell.modules.device.repository.DeviceLifecycleHistoryRepository;
import com.buysell.modules.user.entity.User;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceLifecycleService {

    private final DeviceLifecycleHistoryRepository historyRepository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public void recordEvent(Device device, LifecycleEventType eventType, String reference, String description) {
        User currentUser = null;
        Branch currentBranch = null;
        
        try {
            currentUser = currentUserService.getCurrentUser();
            currentBranch = currentUserService.getCurrentBranch();
        } catch (Exception e) {
            // Might be a system or anonymous event
        }

        DeviceLifecycleHistory history = DeviceLifecycleHistory.builder()
                .device(device)
                .eventType(eventType)
                .eventReference(reference)
                .description(description)
                .performedBy(currentUser)
                .branch(currentBranch)
                .build();

        historyRepository.save(history);

        // Also record to the global audit log
        auditService.logAction(
                currentUser != null ? currentUser.getId() : null,
                currentBranch != null ? currentBranch.getId() : null,
                eventType.name(),
                "Device",
                device.getId(),
                null, // No customer ID
                null,
                null,
                description
        );
    }

    @Transactional(readOnly = true)
    public java.util.List<DeviceLifecycleHistory> getHistory(java.util.UUID deviceId) {
        return historyRepository.findByDeviceIdOrderByCreatedAtDesc(deviceId);
    }
}
