package com.wesleysilva.bappoint.appointments.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RescheduleAppointmentDTO {
    @NotNull
    @FutureOrPresent
    private LocalDate appointmentDate;

    @NotNull
    @Future
    private LocalDateTime startTime;

}
