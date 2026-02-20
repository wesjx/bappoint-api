package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    Integer durationMinutes;

    @Column(name = "is_active", nullable = false)
    Boolean isActive;

}
