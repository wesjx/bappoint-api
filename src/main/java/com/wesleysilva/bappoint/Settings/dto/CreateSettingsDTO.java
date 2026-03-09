package com.wesleysilva.bappoint.Settings.dto;

import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateSettingsDTO {

    @NotNull(message = "Appointment interval is required")
    private AppointmentInterval appointmentInterval;

    @NotNull(message = "Max cancellation interval is required")
    @Min(value = 0, message = "Cancellation interval cannot be negative")
    private Integer maxCancellationInterval;

    @Valid
    private List<CreateServiceDTO> services;

    @Valid
    private List<CreateOperatingHoursDTO> operatingHours;

    @Valid
    private List<CreateOffDayDTO> offDays;
}
