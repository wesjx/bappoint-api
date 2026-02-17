package com.wesleysilva.bappoint.OffDay.dto;

import com.wesleysilva.bappoint.enums.OffDaysType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OffDaysResponseDTO {
    @NotBlank
    @Size(min = 3, max = 500)
    private String reason;

    @NotNull
    @FutureOrPresent
    private LocalDate date;

    @NotNull
    private OffDaysType offDaysType;
}
