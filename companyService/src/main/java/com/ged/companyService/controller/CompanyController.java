package com.ged.companyService.controller;

import com.ged.companyService.dto.CompanyDTO;
import com.ged.companyService.dto.UpdatePermissionsDTO;
import com.ged.companyService.model.Company;
import com.ged.companyService.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<Company> create(@RequestBody CompanyDTO request) {
        Company company = companyService.save(request.name(), request.isVisible());
        return ResponseEntity.ok(company);
    }

    @GetMapping("/{companyId}")
    public Company getCompanyById(@PathVariable UUID companyId) {
        return companyService.findById(companyId);
    }

    @GetMapping("user/{username}")
    public ResponseEntity<List<Company>> getCompaniesByUser(@PathVariable String username) {
        return ResponseEntity.ok(companyService.getCompaniesByUsername(username));
    }

    @PutMapping("/{companyId}/members/{username}/permissions")
    public ResponseEntity<String> updateUserPermissions(
            @PathVariable UUID companyId,
            @PathVariable String username,
            @RequestBody UpdatePermissionsDTO permissionsDTO) throws AccessDeniedException {

        companyService.updateMemberPermissions(companyId, username, permissionsDTO);
        return ResponseEntity.ok("Permissions updated successfully.");
    }

    @PostMapping("/{companyId}/upload")
    public ResponseEntity<String> uploadDocumentCompany(
            @PathVariable UUID companyId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("description") String description,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "customFields", required = false) Map<String, String> customFields) throws AccessDeniedException {
        return companyService.uploadDocument(companyId, file, description, category, tags, customFields);
    }

    @DeleteMapping("/{companyId}/delete")
    public void deleteDocumentCompany(
            @PathVariable UUID companyId,
            @RequestParam("documentName") String documentName
    ) throws AccessDeniedException {
        companyService.deleteDocument(companyId, documentName);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<String> deleteCompany(@PathVariable UUID companyId) throws AccessDeniedException {
        companyService.deleteCompany(companyId);
        return ResponseEntity.ok("Company deleted successfully.");
    }

    @DeleteMapping("/{companyId}/members/{username}")
    public ResponseEntity<String> removeMember(
            @PathVariable UUID companyId,
            @PathVariable String username) throws AccessDeniedException {

        companyService.removeMemberFromCompany(companyId, username);
        return ResponseEntity.ok("Member removed successfully.");
    }
}
