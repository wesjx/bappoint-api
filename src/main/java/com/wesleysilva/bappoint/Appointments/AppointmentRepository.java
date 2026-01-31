package com.wesleysilva.bappoint.Appointments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<AppointmentModel, UUID> {
}
