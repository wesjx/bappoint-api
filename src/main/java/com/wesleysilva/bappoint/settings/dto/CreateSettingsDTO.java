package com.wesleysilva.bappoint.settings.dto;

import com.wesleysilva.bappoint.offday.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.operatinghours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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
