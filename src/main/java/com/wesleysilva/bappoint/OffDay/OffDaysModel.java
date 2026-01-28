package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.enums.OffDaysType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "off_days")
public class OffDayModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "settings_id")
    private SettingsModel settings;

    private String reason;

    private Date date;

    @Enumerated(EnumType.STRING)
    private OffDaysType OffDaystype;
}
