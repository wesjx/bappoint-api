package com.wesleysilva.bappoint.Company.dto;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Settings.SettingsDTO;
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
    private SettingsDTO settings;
    private List<AppointmentReponseDTO> appointments;

}
