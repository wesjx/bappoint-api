package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.OffDay.OffDaysModel;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "settings")
    private CompanyModel company;


    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "appointment_interval")
    private AppointmentInterval appointmentInterval;

    @NotNull
    @Column(name = "max_cancellation_interval")
    private Integer maxCancellationInterval;

    @NotNull
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceModel> services = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperatingHoursModel> operatingHours = new ArrayList<>();

    @NotNull
    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OffDaysModel> offDays = new ArrayList<>();
}
