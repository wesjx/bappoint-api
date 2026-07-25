package com.wesleysilva.bappoint.appointments.dto;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CreateAppointmentDTO {
    @NotBlank
    @Size(min = 2, max = 100)
    private String costumerName;

    @NotBlank
    @Email
    private String costumerEmail;

    @NotBlank
    private String costumerPhone;

    @NotNull
    @Future
    private LocalDateTime startTime;

    @NotEmpty
    private List<UUID> serviceIds;
}
