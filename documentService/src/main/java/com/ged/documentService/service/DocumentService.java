package com.ged.documentService.service;

import com.ged.documentService.client.MetadataFeingClient;
import com.ged.documentService.dto.DocumentMetadataDTO;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {

    private final S3Client s3Client;

    @Value("${cloud.aws.bucketS3.name}")
    private String bucketName;

    private final MetadataFeingClient metadataFeingClient;

    public DocumentService(S3Client s3Client, MetadataFeingClient metadataFeingClient) {
        this.s3Client = s3Client;
        this.metadataFeingClient = metadataFeingClient;
    }

    public void uploadDocument(MultipartFile file, UUID companyId, String username, String description, String category, List<String> tags, Map<String, String> customFields) throws Exception {
        try {
            UUID documentId = UUID.randomUUID();
            String storagePath = companyId + "/" + file.getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(storagePath)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            String checksum = calculateChecksum(file.getBytes());

            DocumentMetadataDTO metadataDTO = DocumentMetadataDTO.builder()
                    .documentId(documentId)
                    .documentName(file.getOriginalFilename())
                    .documentSize(file.getSize())
                    .documentType(file.getContentType())
                    .description(description)
                    .uploadedBy(username)
                    .companyId(companyId)
                    .createdAt(LocalDateTime.now())
                    .checksum(checksum)
                    .tags(tags)
                    .category(category)
                    .customFields(customFields)
                    .build();

            metadataFeingClient.createMetadata(metadataDTO);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void deleteDocument(UUID companyId, String documentName) throws Exception {
        String storagePath = companyId + "/" + documentName;

        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(storagePath)
                .build();

        if (s3Client.headObject(headObjectRequest).hasMetadata()) {
            try {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(storagePath)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);

                metadataFeingClient.deleteMetadata(companyId, documentName);
            } catch (Exception e) {
                throw new Exception("Error message", e);
            }
        } else {
            throw new Exception("File does not exist");
        }
    }

    private String calculateChecksum(byte[] fileData) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(fileData);
    }
}
