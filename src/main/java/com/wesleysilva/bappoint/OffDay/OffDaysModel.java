package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.enums.OffDaysType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "off_days")
public class OffDaysModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "off_days_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settings_id", nullable = false)
    private SettingsModel settings;

    @Column(name = "reason")
    private String reason;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "off_days_type")
    private OffDaysType offDaystype;
}
