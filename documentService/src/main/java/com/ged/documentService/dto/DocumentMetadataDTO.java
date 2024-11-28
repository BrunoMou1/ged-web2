package com.ged.documentService.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
public record DocumentMetadataDTO(UUID documentId, String documentName, String documentType,
        Long documentSize, String uploadedBy, String description, LocalDateTime createdAt, UUID companyId, List<String> tags,
        String category, String checksum, Map<String, String> customFields
) {}
