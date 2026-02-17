package com.wesleysilva.bappoint.Appointments.dto;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateAppointmentDTO {
    @NotBlank
    @Size(min = 2, max = 100)
    private String costumerName;

    @NotBlank
    @Email
    private String costumerEmail;

    @NotBlank
    private String costumerPhone;

    @NotNull
    @FutureOrPresent
    private LocalDate appointmentDate;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotNull
    @Future
    private LocalDateTime endTime;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "99999.99")
    private BigDecimal totalAmount;

    @NotNull
    private AppointmentStatus appointmentStatus;

    @NotEmpty
    private List<UUID> serviceIds;
}
