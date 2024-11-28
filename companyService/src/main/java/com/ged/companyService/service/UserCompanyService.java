package com.ged.companyService.service;

import com.ged.companyService.model.UserCompany;
import com.ged.companyService.repository.UserCompanyRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserCompanyService {

    private final UserCompanyRepository userCompanyRepository;

    public UserCompanyService(UserCompanyRepository userCompanyRepository) {
        this.userCompanyRepository = userCompanyRepository;
    }

    public void save(UserCompany userCompany){
       userCompanyRepository.save(userCompany);
    }

    public List<UserCompany> findByUsername(String username) {
        return userCompanyRepository.findByUsername(username);
    }

    public Optional<UserCompany> findByCompanyIdAndUsername(UUID companyId, String username) {
        return userCompanyRepository.findByCompanyIdAndUsername(companyId, username);
    }

    public void deleteById(UUID id) {
        userCompanyRepository.deleteById(id);
    }
}
