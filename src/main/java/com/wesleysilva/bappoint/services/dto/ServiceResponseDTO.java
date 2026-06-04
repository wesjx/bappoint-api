package com.wesleysilva.bappoint.services.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceResponseDTO {
    @NotNull
    private String name;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "99999.99")
    private BigDecimal price;

    @NotNull
    private Integer durationMinutes;

    @NotNull
    private Boolean isActive;
}
