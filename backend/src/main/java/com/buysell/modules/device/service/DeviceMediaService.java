package com.buysell.modules.device.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.device.entity.Device;
import com.buysell.modules.device.entity.DeviceMedia;
import com.buysell.modules.device.enums.DeviceViewType;
import com.buysell.modules.device.enums.LifecycleEventType;
import com.buysell.modules.device.repository.DeviceMediaRepository;
import com.buysell.modules.media.entity.MediaFile;
import com.buysell.modules.media.service.MediaService;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceMediaService {

    private final DeviceMediaRepository deviceMediaRepository;
    private final DeviceService deviceService;
    private final MediaService mediaService;
    private final DeviceLifecycleService lifecycleService;
    private final CurrentUserService currentUserService;

    @Transactional
    public DeviceMedia addMedia(UUID deviceId, UUID mediaId, DeviceViewType viewType) {
        Device device = deviceService.getDeviceById(deviceId);
        
        if (deviceMediaRepository.existsByDeviceIdAndMediaFileId(deviceId, mediaId)) {
            throw new BusinessException("DEVICE_MEDIA_ALREADY_EXISTS", "This media file is already attached to this device.", HttpStatus.CONFLICT);
        }

        // Will throw if media doesn't exist
        MediaFile mediaFile = mediaService.getMedia(mediaId);

        DeviceMedia deviceMedia = DeviceMedia.builder()
                .device(device)
                .mediaFile(mediaFile)
                .viewType(viewType)
                .createdBy(currentUserService.getCurrentUser())
                .build();

        deviceMedia = deviceMediaRepository.save(deviceMedia);

        lifecycleService.recordEvent(device, LifecycleEventType.MEDIA_ADDED, deviceMedia.getId().toString(), "Added media " + viewType);

        return deviceMedia;
    }

    @Transactional(readOnly = true)
    public List<DeviceMedia> getDeviceMedia(UUID deviceId) {
        return deviceMediaRepository.findByDeviceIdOrderByCreatedAtAsc(deviceId);
    }
}
