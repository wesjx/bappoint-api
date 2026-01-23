package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "company")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @OneToMany(mappedBy = "company")
    private List<ServiceModel> services;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_id")
    private SettingsModel settings;

}
