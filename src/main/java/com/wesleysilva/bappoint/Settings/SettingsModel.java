package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Company.CompanyModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToOne
    @JoinColumn(name = "company_id")
    private CompanyModel companyModel;

    private Integer appointment_interval;

    private Integer max_cancellation_interval;
}
