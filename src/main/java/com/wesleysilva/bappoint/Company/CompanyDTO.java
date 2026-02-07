package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Appointments.AppointmentDTO;
import com.wesleysilva.bappoint.Appointments.AppointmentMapper;
import com.wesleysilva.bappoint.Settings.SettingsDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDTO {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String address;

    private SettingsDTO settings;
    private List<AppointmentDTO> appointments;

}
