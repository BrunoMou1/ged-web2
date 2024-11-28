package ged.api.metadataService.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name="document_metadata")
@Getter
@Setter
public class DocumentMetadata {
    @Id
    @Column(name = "document_id", nullable = false, updatable = false)
    private UUID documentId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(name = "document_size", nullable = false)
    private long documentSize;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "uploaded_by", nullable = false, length = 100)
    private String uploadedBy;

    @Column(name = "company_id", nullable = false, length = 100)
    private UUID companyId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(name = "document_tags", joinColumns = @JoinColumn(name = "document_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "checksum", length = 256)
    private String checksum;

    @ElementCollection
    @CollectionTable(name = "document_custom_fields", joinColumns = @JoinColumn(name = "document_id"))
    @MapKeyColumn(name = "field_key")
    @Column(name = "field_value")
    private Map<String, String> customFields;
}
