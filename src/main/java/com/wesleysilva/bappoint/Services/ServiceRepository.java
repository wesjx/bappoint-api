package com.wesleysilva.bappoint.Services;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceRepository extends JpaRepository<ServiceModel, UUID> {
    List<ServiceModel> findBySettingsId(UUID settingsId);
}
