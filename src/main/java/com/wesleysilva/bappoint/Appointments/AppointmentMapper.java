package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Services.ServiceModel;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class AppointmentMapper {
    public AppointmentDTO toResponseDTO(AppointmentModel appointment) {
        AppointmentDTO AppointmentDto = new AppointmentDTO();
        AppointmentDto.setId(appointment.getId());
        AppointmentDto.setCostumerName(appointment.getCostumerName());
        AppointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        AppointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        AppointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        AppointmentDto.setStartTime(appointment.getStartTime());
        AppointmentDto.setEndTime(appointment.getEndTime());
        AppointmentDto.setTotalAmount(appointment.getTotalAmount());
        AppointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());

        AppointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        AppointmentDto.setCompanyId(appointment.getCompany().getId());
        return AppointmentDto;
    }
}
