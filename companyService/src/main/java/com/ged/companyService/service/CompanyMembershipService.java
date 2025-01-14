package com.ged.companyService.service;

import com.ged.companyService.dto.UpdatePermissionsDTO;
import com.ged.companyService.model.CompanyMembership;
import com.ged.companyService.model.Company;
import com.ged.companyService.model.UserCompany;
import com.ged.companyService.repository.CompanyMembershipRepository;
import com.ged.companyService.service.utils.JwtTokenUtil;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class CompanyMembershipService {

    private final CompanyMembershipRepository companyMembershipRepository;

    private final UserCompanyService userCompanyService;

    private final CompanyService companyService;

    private final JwtTokenUtil jwtTokenUtil;

    public CompanyMembershipService(CompanyMembershipRepository companyMembershipRepository, UserCompanyService userCompanyService, CompanyService companyService, JwtTokenUtil jwtTokenUtil) {
        this.companyMembershipRepository = companyMembershipRepository;
        this.userCompanyService = userCompanyService;
        this.companyService = companyService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public CompanyMembership createRequest(String companyName) {
        CompanyMembership request = new CompanyMembership();

        UUID companyId = companyService.findByName(companyName).getId();

        String token = jwtTokenUtil.getTokenFromHeader();
        String requesterUsername = jwtTokenUtil.extractUsername(token);

        Optional<UserCompany> userCompany = userCompanyService.findByCompanyIdAndUsername(companyId, requesterUsername);

        if (userCompany.isPresent()) {
            throw new RuntimeException("You already are in this company");
        }

        request.setRequesterUsername(requesterUsername);
        request.setCompanyId(companyId);
        request.setRequestDate(LocalDateTime.now());
        request.setStatus("Pendente");
        return companyMembershipRepository.save(request);
    }

    public List<CompanyMembership> getAllRequests(UUID companyId) {
        return companyMembershipRepository.findAllByCompanyId(companyId);
    }

    public void updateRequestStatus(UUID requestId, String newStatus) throws AccessDeniedException {
        CompanyMembership request = companyMembershipRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Company company = companyService.findById(request.getCompanyId());

        String token = jwtTokenUtil.getTokenFromHeader();
        String usernameUserLogged = jwtTokenUtil.extractUsername(token);

        UserCompany currentMember = company.getMembers().stream()
                .filter(member -> member.getUsername().equals(usernameUserLogged))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Member not found in this company"));

        if (!hasManagePermissions(currentMember)) {
            throw new AccessDeniedException("You do not have permission to manage this member's permissions.");
        }

        if ("Aceita".equalsIgnoreCase(newStatus)) {
            UserCompany userCompany = new UserCompany();
            userCompany.setPermissions(Set.of("READ"));
            userCompany.setUsername(request.getRequesterUsername());

            userCompany.setCompany(company);
            userCompanyService.save(userCompany);
            request.setStatus(newStatus);
            companyMembershipRepository.save(request);
        } else {
            companyMembershipRepository.deleteById(requestId);
        }
    }

    private Boolean hasManagePermissions(UserCompany userCompany) {
        return userCompany.getPermissions().contains("MANAGE_USERS") ||
                userCompany.getPermissions().contains("GRANT_PERMISSIONS");
    }
}
