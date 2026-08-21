package com.buysell.modules.media.controller;

import com.buysell.modules.media.enums.MediaType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaResponse {
    private UUID id;
    private MediaType fileType;
    private String mimeType;
    private Long fileSize;
    private String originalFileName;
    // Note: intentionally missing bucket and objectKey to satisfy security requirements
}
