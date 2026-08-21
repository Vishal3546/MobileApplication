package com.buysell.modules.device.repository;

import com.buysell.modules.device.entity.DeviceCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceConditionRepository extends JpaRepository<DeviceCondition, UUID> {
    List<DeviceCondition> findByDeviceIdOrderByCreatedAtDesc(UUID deviceId);
    
    @Query("SELECT dc FROM DeviceCondition dc WHERE dc.device.id = :deviceId ORDER BY dc.createdAt DESC LIMIT 1")
    Optional<DeviceCondition> findLatestByDeviceId(@Param("deviceId") UUID deviceId);
}
