package ged.api.metadataService.controller;

import ged.api.metadataService.model.DocumentMetadata;
import ged.api.metadataService.service.MetadataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/metadata")
public class MetadataController {

    private final MetadataService metadataService;

    public MetadataController(MetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @PostMapping
    public ResponseEntity<DocumentMetadata> createMetadata(@Valid @RequestBody DocumentMetadata metadata) {
        DocumentMetadata createdMetadata = metadataService.createMetadata(metadata);
        return new ResponseEntity<>(createdMetadata, HttpStatus.CREATED);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<List<DocumentMetadata>> getAllMetadataByCompanyId(@PathVariable UUID companyId) {
       return new ResponseEntity<>(metadataService.getAllMetadataByCompanyId(companyId), HttpStatus.ACCEPTED);
    }

    @DeleteMapping
    public void deleteMetadataByCompanyIdAndDocumentname(
            @RequestParam("companyId") UUID companyId,
            @RequestParam("documentName") String documentName
    ){
        metadataService.deleteMetadata(companyId, documentName);
    }
}