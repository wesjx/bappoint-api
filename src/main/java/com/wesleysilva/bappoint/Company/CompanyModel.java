package com.wesleysilva.bappoint.Company;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private String address;
}
