package com.buysell.modules.media.service;

import com.buysell.modules.media.entity.MediaFile;
import com.buysell.modules.media.enums.MediaType;
import com.buysell.modules.media.repository.MediaFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {

    private final StorageService storageService;
    private final MediaFileRepository mediaFileRepository;

    @Transactional
    public MediaFile uploadMedia(MultipartFile file, MediaType type, String bucket) {
        validateFile(file, type);
        
        String objectKey = generateObjectKey(file, type);
        String storedPath = storageService.store(file, bucket, objectKey);
        
        MediaFile mediaFile = MediaFile.builder()
                .storageProvider(storageService.getClass().getSimpleName())
                .bucket(bucket)
                .objectKey(storedPath)
                .fileType(type)
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .originalFileName(file.getOriginalFilename())
                // checksum could be added here
                .build();
                
        return mediaFileRepository.save(mediaFile);
    }

    public Resource loadMediaAsResource(UUID mediaId) {
        MediaFile media = mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));
        return storageService.loadAsResource(media.getBucket(), media.getObjectKey());
    }
    
    public MediaFile getMedia(UUID mediaId) {
        return mediaFileRepository.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found"));
    }

    private void validateFile(MultipartFile file, MediaType type) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        String mimeType = file.getContentType();
        if (mimeType == null) {
            throw new RuntimeException("Content type is missing");
        }

        // Basic validation depending on type
        if (type == MediaType.CUSTOMER_VIDEO && !mimeType.startsWith("video/")) {
            throw new RuntimeException("Invalid video format");
        } else if (type != MediaType.CUSTOMER_VIDEO && !mimeType.startsWith("image/") && !mimeType.equals("application/pdf")) {
            throw new RuntimeException("Invalid file format");
        }
    }

    private String generateObjectKey(MultipartFile file, MediaType type) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return type.name().toLowerCase() + "/" + UUID.randomUUID().toString() + extension;
    }
}
