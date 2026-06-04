package com.wesleysilva.bappoint.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SettingsRepository extends JpaRepository<SettingsModel, UUID> {
}
