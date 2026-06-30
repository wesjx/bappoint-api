package com.wesleysilva.bappoint.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyPublicResponseDto {
    UUID id;
    String name;
    String email;
    String address;
    String phone;
}
