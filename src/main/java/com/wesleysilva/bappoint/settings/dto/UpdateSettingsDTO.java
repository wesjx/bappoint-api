package com.wesleysilva.bappoint.settings.dto;

import com.wesleysilva.bappoint.offday.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.operatinghours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

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
