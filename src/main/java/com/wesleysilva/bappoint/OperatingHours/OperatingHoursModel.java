package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.enums.WeekDay;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "operating_hours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @OneToOne
    @JoinColumn(name = "settings_id")
    private SettingsModel settings;

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday", nullable = false)
    private WeekDay weekday;

    Boolean active;

    Date start_date;
    Date end_date;
}
