package com.wesleysilva.bappoint.company.dto;

import com.wesleysilva.bappoint.services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.settings.dto.SettingsAllDetailsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
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
    String stripeAccountId;
    BigDecimal depositPercentage;
    SettingsAllDetailsDTO settings;
    List<ServiceAllDetailsDTO> services;
}
