package com.wesleysilva.bappoint.Settings.dto;

import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateSettingsDTO {

    @NotNull(message = "Appointment interval is required")
    private AppointmentInterval appointmentInterval;

    @NotNull(message = "Max cancellation interval is required")
    @Min(value = 0, message = "Cancellation interval cannot be negative")
    private Integer maxCancellationInterval;

    @Valid
    private List<ServiceAllDetailsDTO> services;

    @Valid
    private List<OperatingHoursAllDetailsDTO> operatingHours;

    @Valid
    private List<OffDaysAllDetailsDTO> offDays;

}
