package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentModel, UUID> {
    List<AppointmentModel> findByAppointmentDateAndCompanyId(LocalDate date, UUID companyId);

    List<AppointmentModel> findByCompanyId(UUID companyId);

    List<AppointmentModel> findByAppointmentStatusAndCreatedAtBefore(
            AppointmentStatus status,
            LocalDateTime time
    );
}
