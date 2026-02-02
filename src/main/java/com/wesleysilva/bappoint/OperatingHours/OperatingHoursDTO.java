package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.enums.WeekDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursDTO {
    private UUID id;
    private WeekDay weekday;
    private Boolean is_active;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
}
