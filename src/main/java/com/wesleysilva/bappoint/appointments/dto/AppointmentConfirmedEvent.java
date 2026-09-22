package com.wesleysilva.bappoint.appointments.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AppointmentConfirmedEvent(
        UUID appointmentId,
        String customerName,
        String customerEmail,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String companyName
) {}
