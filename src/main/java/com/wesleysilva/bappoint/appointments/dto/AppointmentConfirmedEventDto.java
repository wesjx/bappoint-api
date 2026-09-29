package com.wesleysilva.bappoint.appointments.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentConfirmedEventDto {
    private UUID appointmentId;
    private String customerName;
    private String customerEmail;
    private LocalDate appointmentDate;
    private LocalDateTime appointmentTime;
    private String companyName;
}
