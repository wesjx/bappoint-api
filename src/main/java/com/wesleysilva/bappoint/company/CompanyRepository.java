package com.wesleysilva.bappoint.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository extends JpaRepository<CompanyModel, UUID> {
    boolean existsByEmail(String email);
    Optional<CompanyModel> findByClerkUserId(String clerkUserId);
    List<CompanyModel> findAllByClerkUserId(String clerkUserId);
    Optional<CompanyModel> findBySlug(String slug);
}
