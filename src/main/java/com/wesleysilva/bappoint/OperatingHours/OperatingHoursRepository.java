package com.wesleysilva.bappoint.OperatingHours;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OperatingHoursRepository extends JpaRepository<OperatingHoursModel, UUID> {
}
