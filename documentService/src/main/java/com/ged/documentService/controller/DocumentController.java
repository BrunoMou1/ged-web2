package com.ged.documentService.controller;

import com.ged.documentService.service.DocumentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") UUID companyId,
            @RequestParam("username") String username,
            @RequestParam("description") String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "customFields", required = false) Map<String, String> customFields) {

        try {
            documentService.uploadDocument(file, companyId, username, description, category, tags, customFields);
            return new ResponseEntity<>("Upload successful", HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/delete")
    public void deleteDocument(
            @RequestParam("companyId") UUID companyId,
            @RequestParam("documentName") String documentName
    ) throws Exception {
        documentService.deleteDocument(companyId, documentName);
    }
}