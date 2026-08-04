package com.wesleysilva.bappoint.company.dto;

import com.wesleysilva.bappoint.appointments.dto.AppointmentResponseDTO;
import com.wesleysilva.bappoint.enums.PaymentSetupStatus;
import com.wesleysilva.bappoint.settings.dto.SettingsAllDetailsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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

    private String stripeAccountId;

    private PaymentSetupStatus paymentSetupStatus;

    private String stripeConnectedAt;

    private String stripeConnectionError;

    @NotNull
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal depositPercentage;

    @Valid
    @NotNull
    private SettingsAllDetailsDTO settings;

    @Valid
    @NotNull
    private List<AppointmentResponseDTO> appointments;

    @NotEmpty
    private String clerkUserId;

    @NotEmpty
    private String slug;

}
