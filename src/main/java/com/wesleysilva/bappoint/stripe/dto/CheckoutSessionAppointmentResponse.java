package com.wesleysilva.bappoint.stripe;

import com.wesleysilva.bappoint.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CheckoutSessionAppointmentResponse(
        UUID id,
        AppointmentStatus appointmentStatus,
        LocalDate appointmentDate,
        LocalDateTime startTime,
        String costumerName,
        List<String> services,
        String companyName
) {
}
