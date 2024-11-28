package com.ged.companyService.repository;

import com.ged.companyService.model.CompanyMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompanyMembershipRepository extends JpaRepository<CompanyMembership, UUID> {
    List<CompanyMembership> findAllByCompanyId(UUID groupId);
}
