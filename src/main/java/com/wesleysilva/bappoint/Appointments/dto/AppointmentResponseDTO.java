package com.wesleysilva.bappoint.Appointments.dto;

import com.wesleysilva.bappoint.enums.AppointmentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AppointmentResponseDTO {
    private UUID id;
    private String costumerName;
    private String costumerEmail;
    private String costumerPhone;
    private LocalDate appointmentDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private AppointmentStatus appointmentStatus;

    private List<UUID> serviceIds;
    private UUID companyId;
}
