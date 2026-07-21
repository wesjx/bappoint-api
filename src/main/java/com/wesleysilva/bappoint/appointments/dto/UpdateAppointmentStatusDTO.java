package com.wesleysilva.bappoint.appointments.dto;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateAppointmentStatusDTO {
    @NotNull
    private AppointmentStatus status;
}
