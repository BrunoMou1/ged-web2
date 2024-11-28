package com.ged.companyService.dto;

import lombok.Builder;

@Builder
public record CompanyDTO(String name, Boolean isVisible) {
}
