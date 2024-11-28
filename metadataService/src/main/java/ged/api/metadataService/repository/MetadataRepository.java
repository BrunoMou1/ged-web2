package ged.api.metadataService.repository;

import ged.api.metadataService.model.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MetadataRepository extends JpaRepository<DocumentMetadata, UUID> {
    List<DocumentMetadata> getAllMetadataByCompanyId(UUID companyId);

    void deleteMetadataByCompanyIdAndDocumentName(UUID companyId, String documentName);
}
