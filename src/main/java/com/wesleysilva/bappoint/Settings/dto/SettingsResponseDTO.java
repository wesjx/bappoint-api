package com.wesleysilva.bappoint.Settings.dto;

import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SettingsResponseDTO {
    private UUID id;

    @NotNull
    private AppointmentInterval appointmentInterval;

    @NotNull
    private Integer maxCancellationInterval;
}
