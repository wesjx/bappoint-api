package com.wesleysilva.bappoint.company.dto;

import com.wesleysilva.bappoint.settings.dto.CreateSettingsDTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal depositPercentage;

    private String clerkUserId;

    @NotEmpty
    private String slug;
}
