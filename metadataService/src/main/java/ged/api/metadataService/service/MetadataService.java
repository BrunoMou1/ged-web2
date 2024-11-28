package ged.api.metadataService.service;

import ged.api.metadataService.model.DocumentMetadata;
import ged.api.metadataService.repository.MetadataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MetadataService {

    private final MetadataRepository metadataRepository;

    public MetadataService(MetadataRepository metadataRepository) {
        this.metadataRepository = metadataRepository;
    }

    public DocumentMetadata createMetadata(DocumentMetadata metadata) {
        return metadataRepository.save(metadata);
    }

    public List<DocumentMetadata> getAllMetadataByCompanyId(UUID companyId) {
        return metadataRepository.getAllMetadataByCompanyId(companyId);
    }

    @Transactional
    public void deleteMetadata(UUID companyId, String documentName) {
        metadataRepository.deleteMetadataByCompanyIdAndDocumentName(companyId, documentName);
    }
}
