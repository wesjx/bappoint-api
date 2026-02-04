package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "appointments")
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id")
    private UUID id;

    private String costumerName;
    private String costumerEmail;
    private String costumerPhone;

    private LocalDate appointmentDate;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private double totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status")
    private AppointmentStatus appointmentStatus;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "appointment_services",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceModel> services;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyModel company;

}
