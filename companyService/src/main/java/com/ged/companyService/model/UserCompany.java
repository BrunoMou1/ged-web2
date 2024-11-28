package com.ged.companyService.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "user_company")
@Table(name = "user_company")
@Getter
@Setter
public class UserCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @JsonBackReference
    private Company company;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_company_permissions", joinColumns = @JoinColumn(name = "user_company_id"))
    @Column(name = "permission") //MANAGE_USERS, GRANT_PERMISSIONS, READ, UPLOAD
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private Set<String> permissions = new HashSet<>();

}
