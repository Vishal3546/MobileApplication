package com.buysell.modules.media.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    
    /**
     * Stores a file and returns its generated object key or path relative to the bucket/storage root.
     */
    String store(MultipartFile file, String bucket, String generateObjectKey);

    /**
     * Loads a file as a Resource (e.g. for streaming).
     */
    Resource loadAsResource(String bucket, String objectKey);
    
    /**
     * Loads a file as an InputStream.
     */
    InputStream loadAsInputStream(String bucket, String objectKey);

    /**
     * Deletes a file.
     */
    void delete(String bucket, String objectKey);
    
    /**
     * Checks if a file exists.
     */
    boolean exists(String bucket, String objectKey);
    
    /**
     * Generates a pre-signed temporary access URL (if supported by provider).
     * For Local FS, it might return a secure internal endpoint path.
     */
    String generatePreSignedUrl(String bucket, String objectKey, int expirySeconds);
}
