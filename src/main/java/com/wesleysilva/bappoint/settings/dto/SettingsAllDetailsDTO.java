package com.wesleysilva.bappoint.settings.dto;

import com.wesleysilva.bappoint.offday.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.operatinghours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SettingsAllDetailsDTO {

    @NotNull(message = "Id cannot be null")
    private UUID id;

    @NotNull(message = "Appointment interval is required")
    private AppointmentInterval appointmentInterval;

    @NotNull(message = "Max cancellation interval is required")
    @Min(value = 0, message = "Cancellation interval cannot be negative")
    private Integer maxCancellationInterval;

    @NotEmpty(message = "Services cannot be empty")
    @Valid
    private List<ServiceAllDetailsDTO> services;

    @NotEmpty(message = "Operating hours cannot be empty")
    @Valid
    private List<OperatingHoursAllDetailsDTO> operatingHours;

    @Valid
    private List<OffDaysAllDetailsDTO> offDays;
}

