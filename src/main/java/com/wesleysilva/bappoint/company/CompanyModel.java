package com.wesleysilva.bappoint.company;

import com.wesleysilva.bappoint.appointments.AppointmentModel;
import com.wesleysilva.bappoint.settings.SettingsModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "company_id")
    private UUID id;

    @Column(unique = true)
    private String slug;

    @Column(name = "stripe_account_id")
    private String stripeAccountId;

    @Column(name = "clerk_user_id")
    private String clerkUserId;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "deposit_percentage")
    private BigDecimal depositPercentage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_id")
    private SettingsModel settings;

    @OneToMany(mappedBy = "company")
    private List<AppointmentModel> appointments;

}
