package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentsDTO {
    private UUID id;

    private String costumerName;
    private String costumerEmail;
    private String costumerPhone;

    private Date appointmentDate;

    private long startTime;
    private long endTime;

    private double totalAmount;

    private AppointmentStatus appointmentStatus;
}
