package com.buysell.modules.device.repository;

import com.buysell.modules.device.entity.DeviceMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceMediaRepository extends JpaRepository<DeviceMedia, UUID> {
    List<DeviceMedia> findByDeviceIdOrderByCreatedAtAsc(UUID deviceId);
    boolean existsByDeviceIdAndMediaFileId(UUID deviceId, UUID mediaFileId);
}
