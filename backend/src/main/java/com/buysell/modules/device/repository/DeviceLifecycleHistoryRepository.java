package com.buysell.modules.device.repository;

import com.buysell.modules.device.entity.DeviceLifecycleHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceLifecycleHistoryRepository extends JpaRepository<DeviceLifecycleHistory, UUID> {
    List<DeviceLifecycleHistory> findByDeviceIdOrderByCreatedAtDesc(UUID deviceId);
}
