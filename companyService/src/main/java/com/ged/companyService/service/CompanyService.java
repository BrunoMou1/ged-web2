package com.ged.companyService.service;

import com.ged.companyService.client.DocumentFeignClient;
import com.ged.companyService.dto.UpdatePermissionsDTO;
import com.ged.companyService.model.Company;
import com.ged.companyService.model.UserCompany;
import com.ged.companyService.repository.CompanyRepository;
import com.ged.companyService.service.utils.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final UserCompanyService userCompanyService;

    private final JwtTokenUtil jwtTokenUtil;

    private final DocumentFeignClient documentFeignClient;


    public CompanyService(CompanyRepository companyRepository, UserCompanyService userCompanyService, JwtTokenUtil jwtTokenUtil, DocumentFeignClient documentFeignClient) {
        this.companyRepository = companyRepository;
        this.userCompanyService = userCompanyService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.documentFeignClient = documentFeignClient;
    }

    @Transactional
    public Company save(String name, Boolean isVisible) {
        String token = jwtTokenUtil.getTokenFromHeader();
        String createdBy = jwtTokenUtil.extractUsername(token);
        Company newCompany = new Company(name, createdBy, isVisible);

        UserCompany userCompany = new UserCompany();
        userCompany.setPermissions(Set.of("MANAGE_USERS", "GRANT_PERMISSIONS", "READ", "UPLOAD"));
        userCompany.setUsername(createdBy);
        userCompany.setCompany(newCompany);
        userCompanyService.save(userCompany);

        return companyRepository.save(newCompany);
    }

    public Company findById(UUID companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
    }


    public List<Company> getCompaniesByUsername(String username) {
        List<UserCompany> userCompanies = userCompanyService.findByUsername(username);
        return userCompanies.stream()
                .map(UserCompany::getCompany)
                .collect(Collectors.toList());
    }

    public void updateMemberPermissions(UUID companyId, String username, UpdatePermissionsDTO permissionsDTO) throws AccessDeniedException {
        Company company = findById(companyId);

        UserCompany userCompany = userCompanyService.findByCompanyIdAndUsername(companyId, username)
                .orElseThrow(() -> new RuntimeException("Member not found in this company"));

        String token = jwtTokenUtil.getTokenFromHeader();
        String usernameUserLogged = jwtTokenUtil.extractUsername(token);

        UserCompany currentMember = checkIfLoggedUserIsInCompany(company, usernameUserLogged);

        if (!hasPermissions(currentMember, List.of("GRANT_PERMISSIONS"))) {
            throw new AccessDeniedException("You do not have permission to manage this member's permissions.");
        }

        userCompany.setPermissions(permissionsDTO.permissions());
        userCompanyService.save(userCompany);
    }

    @Transactional
    public void removeMemberFromCompany(UUID companyId, String username) throws AccessDeniedException {
        Company company = findById(companyId);

        UserCompany userCompany = userCompanyService.findByCompanyIdAndUsername(companyId, username)
                .orElseThrow(() -> new RuntimeException("Member not found in this company"));

        String token = jwtTokenUtil.getTokenFromHeader();
        String usernameUserLogged = jwtTokenUtil.extractUsername(token);

        UserCompany currentMember = checkIfLoggedUserIsInCompany(company, usernameUserLogged);

        if (!hasPermissions(currentMember, Arrays.asList("GRANT_PERMISSIONS", "MANAGE_USERS"))) {
            throw new AccessDeniedException("You do not have permission to delete this company.");
        }

        userCompanyService.deleteById(userCompany.getId());
    }


    public void deleteCompany(UUID companyId) throws AccessDeniedException {
        Company company = findById(companyId);

        String token = jwtTokenUtil.getTokenFromHeader();
        String usernameUserLogged = jwtTokenUtil.extractUsername(token);

        UserCompany currentMember = checkIfLoggedUserIsInCompany(company, usernameUserLogged);

        if (!hasPermissions(currentMember, List.of("GRANT_PERMISSIONS"))) {
            throw new AccessDeniedException("You do not have permission to delete this company.");
        }

        companyRepository.deleteById(companyId);
    }

    public ResponseEntity<String> uploadDocument(UUID companyId, MultipartFile file, String description, String category, List<String> tags, Map<String, String> customFields) throws AccessDeniedException {
        String token = jwtTokenUtil.getTokenFromHeader();
        String usernameUserLogged = jwtTokenUtil.extractUsername(token);

        UserCompany userCompany = userCompanyService.findByCompanyIdAndUsername(companyId, usernameUserLogged)
                .orElseThrow(() -> new RuntimeException("Member not found in this company"));

        if(!hasPermissions(userCompany, Arrays.asList("GRANT_PERMISSIONS", "UPLOAD"))) {
            throw new AccessDeniedException("You do not have permission to upload a document.");
        }

        return documentFeignClient.uploadDocument(file, companyId, description, usernameUserLogged, category, tags, customFields);
    }

    public void deleteDocument(UUID companyId, String documentName) throws AccessDeniedException {
        String token = jwtTokenUtil.getTokenFromHeader();
        String usernameUserLogged = jwtTokenUtil.extractUsername(token);

        UserCompany userCompany = userCompanyService.findByCompanyIdAndUsername(companyId, usernameUserLogged)
                .orElseThrow(() -> new RuntimeException("Member not found in this company"));

        if(!hasPermissions(userCompany, Arrays.asList("GRANT_PERMISSIONS", "UPLOAD"))) {
            throw new AccessDeniedException("You do not have permission to delete a document.");
        }

        documentFeignClient.deleteDocument(companyId, documentName);
    }

    private Boolean hasPermissions(UserCompany userCompany, List<String> permissions) {
        return userCompany.getPermissions().stream()
                .anyMatch(permissions::contains);
    }


    private UserCompany checkIfLoggedUserIsInCompany(Company company, String username) {
        return company.getMembers().stream()
                .filter(member -> member.getUsername().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You are not in this company"));
    }

}
