package com.ged.companyService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "document-service")
public interface DocumentFeignClient {
    @PostMapping(value = "/v1/api/documents/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestParam("companyId") UUID companyId,
            @RequestParam("username") String username,
            @RequestParam("description") String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "customFields", required = false) Map<String, String> customFields);

    @DeleteMapping("/v1/api/documents/delete")
    void deleteDocument(
            @RequestParam("companyId") UUID companyId,
            @RequestParam("documentName") String documentName
    );
}