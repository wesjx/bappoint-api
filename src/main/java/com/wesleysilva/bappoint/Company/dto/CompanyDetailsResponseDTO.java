package com.wesleysilva.bappoint.Company.dto;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDetailsResponseDTO {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String address;

    @Valid
    @NotNull
    private String stripeAccountId;

    @Valid
    @NotNull
    private SettingsAllDetailsDTO settings;

    @Valid
    @NotNull
    private List<AppointmentReponseDTO> appointments;

    @NotEmpty
    private String clerkUserId;

}
