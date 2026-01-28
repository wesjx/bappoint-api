package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.OffDay.OffDaysModel;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.Services.ServiceModel;
import jakarta.persistence.*;
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

    private Integer appointment_interval;

    private Integer max_cancellation_interval;

    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceModel> services = new ArrayList<>();

    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperatingHoursModel> operatingHours = new ArrayList<>();

    @OneToMany(mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OffDaysModel> offDays = new ArrayList<>();
}
