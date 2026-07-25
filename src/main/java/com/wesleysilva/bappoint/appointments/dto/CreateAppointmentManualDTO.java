package com.wesleysilva.bappoint.appointments.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CreateAppointmentManualDTO {
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
