package com.buysell.modules.media.controller;

import com.buysell.common.dto.ApiResponse;
import com.buysell.modules.media.entity.MediaFile;
import com.buysell.modules.media.enums.MediaType;
import com.buysell.modules.media.service.MediaService;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;
    private final AuditService auditService;
    private final CurrentUserService currentUserService;

    @PostMapping
    @PreAuthorize("hasAuthority('UPLOAD_MEDIA')")
    public ResponseEntity<ApiResponse<MediaResponse>> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MediaType type,
            @RequestParam(value = "bucket", defaultValue = "default") String bucket) {
        
        MediaFile mediaFile = mediaService.uploadMedia(file, type, bucket);
        
        return ResponseEntity.ok(ApiResponse.success(MediaResponse.builder()
                .id(mediaFile.getId())
                .fileType(mediaFile.getFileType())
                .mimeType(mediaFile.getMimeType())
                .fileSize(mediaFile.getFileSize())
                .originalFileName(mediaFile.getOriginalFileName())
                .build()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_MEDIA')")
    public ResponseEntity<Resource> downloadMedia(@PathVariable UUID id) {
        Resource resource = mediaService.loadMediaAsResource(id);
        MediaFile mediaInfo = mediaService.getMedia(id);
        
        UUID branchId = null;
        try {
            branchId = currentUserService.getCurrentBranch().getId();
        } catch (Exception e) {
            // Global/Super Admin user without a specific branch
        }
        
        auditService.logAction(
            currentUserService.getCurrentUserId(), 
            branchId,
            "MEDIA_ACCESSED", 
            "MediaFile", 
            id, 
            null, 
            null, 
            null, 
            null
        );
        
        // This acts as our secure streaming endpoint
        // Doesn't expose bucket or exact path to client natively
        
        org.springframework.http.MediaType mediaType = MediaTypeFactory
                .getMediaType(resource)
                .orElse(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
                
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mediaInfo.getOriginalFileName() + "\"")
                .body(resource);
    }
}
