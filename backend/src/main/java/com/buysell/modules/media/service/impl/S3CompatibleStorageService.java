package com.buysell.modules.media.service.impl;

import com.buysell.modules.media.service.StorageService;
import org.springframework.core.io.Resource;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

// @Service("s3StorageService") // Uncomment and configure when S3 is needed
public class S3CompatibleStorageService implements StorageService {

    @Override
    public String store(MultipartFile file, String bucket, String generateObjectKey) {
        throw new UnsupportedOperationException("S3 Storage not yet configured");
    }

    @Override
    public Resource loadAsResource(String bucket, String objectKey) {
        throw new UnsupportedOperationException("S3 Storage not yet configured");
    }

    @Override
    public InputStream loadAsInputStream(String bucket, String objectKey) {
        throw new UnsupportedOperationException("S3 Storage not yet configured");
    }

    @Override
    public void delete(String bucket, String objectKey) {
        throw new UnsupportedOperationException("S3 Storage not yet configured");
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        throw new UnsupportedOperationException("S3 Storage not yet configured");
    }

    @Override
    public String generatePreSignedUrl(String bucket, String objectKey, int expirySeconds) {
        throw new UnsupportedOperationException("S3 Storage not yet configured");
    }
}
