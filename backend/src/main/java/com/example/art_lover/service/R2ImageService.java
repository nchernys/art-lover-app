package com.example.art_lover.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.art_lover.dto.r2.R2ImageUploadResponse;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class R2ImageService {

    private final S3Client s3Client;

    @Value("${r2.bucket}")
    private String bucket;

    @Value("${r2.account-id}")
    private String accountId;

    public R2ImageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public R2ImageUploadResponse uploadImage(
            InputStream inputStream,
            long size,
            String key,
            String contentType) {

        try {

            // build R2 request
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength(size)
                    .build();

            // upload to R2
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(inputStream, size));

            // build public URL
            String url = "https://" + bucket + "." + accountId + ".r2.dev/" + key;

            return new R2ImageUploadResponse(url, key);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image to Cloudflare R2", e);
        }
    }

    public void deleteImage(String key) {

        System.out.println("IMAGE KEY" + " " + key);
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }
}
