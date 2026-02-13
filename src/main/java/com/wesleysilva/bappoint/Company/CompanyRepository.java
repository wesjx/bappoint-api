package com.wesleysilva.bappoint.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<CompanyModel, UUID> {
    boolean existsByEmail(String email);
}
