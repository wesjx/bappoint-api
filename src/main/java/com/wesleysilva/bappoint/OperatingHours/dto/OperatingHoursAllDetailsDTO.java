package com.wesleysilva.bappoint.OperatingHours.dto;

import com.wesleysilva.bappoint.enums.WeekDay;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class OperatingHoursAllDetailsDTO {
    private UUID id;

    @Enumerated(EnumType.STRING)
    @NotBlank
    @Size(min = 1, max = 10)
    private WeekDay weekday;

    @NotNull
    private Boolean isActive;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private LocalTime lunchStartTime;

    private LocalTime lunchEndTime;
}
