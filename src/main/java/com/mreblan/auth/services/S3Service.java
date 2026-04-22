package com.mreblan.auth.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file, String folderPrefix) throws IOException {
        String key = folderPrefix + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        s3Client.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), metadata));
        log.info("File uploaded to S3: {}", key);
        return key;
    }

    public String generatePresignedUrl(String key, int expirationMinutes) {
        Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000L);
        URL url = s3Client.generatePresignedUrl(bucket, key, expiration);
        return url.toString();
    }

    public void deleteFile(String key) {
        if (key != null) {
            s3Client.deleteObject(bucket, key);
            log.info("Deleted from S3: {}", key);
        }
    }
}