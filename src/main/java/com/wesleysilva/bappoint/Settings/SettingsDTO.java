package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Services.ServiceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingsDTO {

    private UUID id;

    private Integer appointment_interval;

    private Integer max_cancellation_interval;

    private List<ServiceDTO> services;
}
