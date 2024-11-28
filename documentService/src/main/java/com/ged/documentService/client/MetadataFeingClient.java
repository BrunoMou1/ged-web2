package com.ged.documentService.client;

import com.ged.documentService.dto.DocumentMetadataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "metadata-service")
public interface MetadataFeingClient {
    @PostMapping("/v1/api/metadata")
    ResponseEntity<DocumentMetadataDTO> createMetadata(@RequestBody DocumentMetadataDTO documentMetadataDTO);

    @DeleteMapping("/v1/api/metadata")
    void deleteMetadata(@RequestParam("companyId") UUID companyId, @RequestParam("documentName") String documentName);
}
