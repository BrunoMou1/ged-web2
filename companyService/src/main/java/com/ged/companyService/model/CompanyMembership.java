package com.ged.companyService.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "group_membership_requests")
@Table(name = "group_membership_requests")
@Getter
@Setter
public class CompanyMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "requester_username", nullable = false)
    private String requesterUsername;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;

    @Column(name = "status", nullable = false)
    private String status; // Pendente, Aceita, Rejeitada
}