package com.buysell.modules.media.service.impl;

import com.buysell.modules.media.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileSystemStorageService implements StorageService {

    @Value("${app.storage.local.root-dir:/data/media}")
    private String rootDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(rootDir);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory", e);
        }
    }

    @Override
    public String store(MultipartFile file, String bucket, String generateObjectKey) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            Path bucketPath = this.rootLocation.resolve(bucket);
            Files.createDirectories(bucketPath);
            
            Path destinationFile = bucketPath.resolve(Paths.get(generateObjectKey))
                    .normalize().toAbsolutePath();
            
            if (!destinationFile.getParent().toAbsolutePath().startsWith(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            Files.createDirectories(destinationFile.getParent());
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
            
            return generateObjectKey;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public Resource loadAsResource(String bucket, String objectKey) {
        try {
            Path file = rootLocation.resolve(bucket).resolve(objectKey);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + objectKey);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + objectKey, e);
        }
    }

    @Override
    public InputStream loadAsInputStream(String bucket, String objectKey) {
        try {
            Resource resource = loadAsResource(bucket, objectKey);
            return resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load input stream for: " + objectKey, e);
        }
    }

    @Override
    public void delete(String bucket, String objectKey) {
        try {
            Path file = rootLocation.resolve(bucket).resolve(objectKey);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + objectKey, e);
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        Path file = rootLocation.resolve(bucket).resolve(objectKey);
        return Files.exists(file);
    }

    @Override
    public String generatePreSignedUrl(String bucket, String objectKey, int expirySeconds) {
        // Local implementation doesn't use pre-signed URLs directly in the same way as S3.
        // We return the secure backend endpoint that streams it.
        return "/api/v1/media/stream?bucket=" + bucket + "&key=" + objectKey;
    }
}
