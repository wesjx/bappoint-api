package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Services.ServiceModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AppointmentMapper {
    public AppointmentModel toEntity(AppointmentDTO appointmentsDTO) {
        AppointmentModel appointmentModel = new AppointmentModel();
        appointmentModel.setId(appointmentsDTO.getId());
        appointmentModel.setCostumerName(appointmentsDTO.getCostumerName());
        appointmentModel.setCostumerEmail(appointmentsDTO.getCostumerEmail());
        appointmentModel.setCostumerPhone(appointmentsDTO.getCostumerPhone());
        appointmentModel.setStartTime(appointmentsDTO.getStartTime());
        appointmentModel.setAppointmentStatus(appointmentsDTO.getAppointmentStatus());

        return appointmentModel;
    }

    public AppointmentDTO toDto(AppointmentModel appointmentModel) {
        AppointmentDTO appointmentsDTO = new AppointmentDTO();
        appointmentsDTO.setId(appointmentModel.getId());
        appointmentsDTO.setCostumerName(appointmentModel.getCostumerName());
        appointmentsDTO.setCostumerEmail(appointmentModel.getCostumerEmail());
        appointmentsDTO.setCostumerPhone(appointmentModel.getCostumerPhone());
        appointmentsDTO.setStartTime(appointmentModel.getStartTime());
        appointmentsDTO.setAppointmentStatus(appointmentModel.getAppointmentStatus());

        return appointmentsDTO;
    }

    public AppointmentResponseDTO toResponseDTO(AppointmentModel appointment) {
        AppointmentResponseDTO dto = new AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setCostumerName(appointment.getCostumerName());
        dto.setCostumerEmail(appointment.getCostumerEmail());
        dto.setCostumerPhone(appointment.getCostumerPhone());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setTotalAmount(appointment.getTotalAmount());
        dto.setAppointmentStatus(appointment.getAppointmentStatus());

        dto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        dto.setCompanyId(appointment.getCompany().getId());
        return dto;
    }
}
