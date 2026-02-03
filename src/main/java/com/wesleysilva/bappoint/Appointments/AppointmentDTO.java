package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {
    private UUID id;

    private String costumerName;
    private String costumerEmail;
    private String costumerPhone;

    private LocalDateTime appointmentDate;

    private long startTime;
    private long endTime;

    private double totalAmount;

    private AppointmentStatus appointmentStatus;
}
