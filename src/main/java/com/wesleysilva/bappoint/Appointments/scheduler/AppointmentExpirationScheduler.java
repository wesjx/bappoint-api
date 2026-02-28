package com.wesleysilva.bappoint.Appointments.scheduler;

import com.wesleysilva.bappoint.Appointments.AppointmentModel;
import com.wesleysilva.bappoint.Appointments.AppointmentRepository;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class AppointmentExpirationScheduler {

    private final AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 60000)
    public void cancelExpiredAppointments() {

        List<AppointmentModel> expired =
                appointmentRepository.findByAppointmentStatusAndCreatedAtBefore(
                        AppointmentStatus.PENDING,
                        LocalDateTime.now()
                );

        for (AppointmentModel appointment : expired) {

            appointment.setAppointmentStatus(AppointmentStatus.NOT_PAID);
            appointmentRepository.save(appointment);
        }
    }
}
