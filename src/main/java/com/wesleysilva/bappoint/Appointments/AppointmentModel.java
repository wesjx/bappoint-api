package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "appointment")
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "company_id")
    private CompanyModel company;

    @OneToOne
    @JoinColumn(name = "settings_id")
    private SettingsModel settings;

    private String costumerName;
    private String costumerEmail;
    private String costumerPhone;

    private Date appointmentDate;

    private long startTime;
    private long endTime;

    private double totalAmount;

    private AppointmentStatus status;

}
