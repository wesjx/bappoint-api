package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(name = "costumer_name", nullable = false, length = 100)
    private String costumerName;

    @Column(name = "costumer_email", nullable = false, length = 150)
    private String costumerEmail;

    @Column(name = "costumer_phone", nullable = false, length = 30)
    private String costumerPhone;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_status", nullable = false)
    private AppointmentStatus appointmentStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "appointment_services",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private List<ServiceModel> services;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyModel company;
}

