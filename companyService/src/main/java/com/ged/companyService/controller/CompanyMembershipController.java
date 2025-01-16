package com.ged.companyService.controller;

import com.ged.companyService.dto.UpdatePermissionsDTO;
import com.ged.companyService.model.CompanyMembership;
import com.ged.companyService.service.CompanyMembershipService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/api/companymembership")
public class CompanyMembershipController {

    private final CompanyMembershipService companyMembershipService;

    public CompanyMembershipController(CompanyMembershipService companyMembershipService) {
        this.companyMembershipService = companyMembershipService;
    }

    @PostMapping("/{companyName}")
    public ResponseEntity<CompanyMembership> createRequest(
            @PathVariable String companyName) {
        CompanyMembership newRequest = companyMembershipService.createRequest(companyName);
        return ResponseEntity.ok(newRequest);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<List<CompanyMembership>> getAllRequests(@PathVariable UUID companyId) {
        List<CompanyMembership> requests = companyMembershipService.getAllRequests(companyId);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/{requestId}")
    public void updateRequestStatus(
            @PathVariable UUID requestId,
            @RequestParam String newStatus) throws AccessDeniedException {
        companyMembershipService.updateRequestStatus(requestId, newStatus);
    }
}
