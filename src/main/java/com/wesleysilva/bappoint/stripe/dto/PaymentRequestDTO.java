package com.wesleysilva.bappoint.stripe.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull
    private UUID appointmentId;
}
