package com.wesleysilva.bappoint.Company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;

}
