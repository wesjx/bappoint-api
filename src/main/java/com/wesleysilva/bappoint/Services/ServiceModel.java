package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "settings_id")
    private SettingsModel settings;

    String name;
    Integer price;
    Integer duration_minutes;
    Boolean is_active;

}
