package com.wesleysilva.bappoint.Company.dto;

import com.wesleysilva.bappoint.Settings.dto.CreateSettingsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCompanyDTO {

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

    @NotNull
    private CreateSettingsDTO settings;

    @NotNull
    private String stripeAccountId;

    @NotEmpty
    private String clerkUserId;

}
