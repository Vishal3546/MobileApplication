package com.buysell.modules.device.repository;

import com.buysell.modules.device.entity.DeviceInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceInspectionRepository extends JpaRepository<DeviceInspection, UUID> {
    List<DeviceInspection> findByDeviceIdOrderByCreatedAtDesc(UUID deviceId);
    
    @Query("SELECT di FROM DeviceInspection di WHERE di.device.id = :deviceId ORDER BY di.createdAt DESC LIMIT 1")
    Optional<DeviceInspection> findLatestByDeviceId(@Param("deviceId") UUID deviceId);
}
