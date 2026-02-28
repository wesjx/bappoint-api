package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Services.ServiceModel;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class AppointmentMapper {
    public AppointmentAllDetailsDTO toResponseAllDetailsDTO(AppointmentModel appointment) {
        AppointmentAllDetailsDTO AppointmentDto = new AppointmentAllDetailsDTO();
        AppointmentDto.setId(appointment.getId());
        AppointmentDto.setCostumerName(appointment.getCostumerName());
        AppointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        AppointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        AppointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        AppointmentDto.setStartTime(appointment.getStartTime());
        AppointmentDto.setEndTime(appointment.getEndTime());
        AppointmentDto.setTotalAmount(appointment.getTotalAmount());
        AppointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        AppointmentDto.setStripeSessionId(appointment.getStripeSessionId());

        AppointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        AppointmentDto.setCompanyId(appointment.getCompany().getId());
        return AppointmentDto;
    }

    public CreateAppointmentDTO toCreateAppointmentDTO(AppointmentModel appointment) {
        CreateAppointmentDTO AppointmentDto = new CreateAppointmentDTO();

        AppointmentDto.setCostumerName(appointment.getCostumerName());
        AppointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        AppointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        AppointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        AppointmentDto.setStartTime(appointment.getStartTime());
        AppointmentDto.setEndTime(appointment.getEndTime());
        AppointmentDto.setTotalAmount(appointment.getTotalAmount());
        AppointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        AppointmentDto.setStripeSessionId(appointment.getStripeSessionId());
        AppointmentDto.setCreatedAt(appointment.getCreatedAt());

        AppointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        AppointmentDto.setCompanyId(appointment.getCompany().getId());
        return AppointmentDto;
    }

    public UpdateAppointmentDTO toUpdateAppointmentDTO(AppointmentModel appointment) {
        UpdateAppointmentDTO AppointmentDto = new UpdateAppointmentDTO();

        AppointmentDto.setCostumerName(appointment.getCostumerName());
        AppointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        AppointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        AppointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        AppointmentDto.setStartTime(appointment.getStartTime());
        AppointmentDto.setEndTime(appointment.getEndTime());
        AppointmentDto.setTotalAmount(appointment.getTotalAmount());
        AppointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        AppointmentDto.setStripeSessionId(appointment.getStripeSessionId());

        AppointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        return AppointmentDto;
    }

    public AppointmentReponseDTO toResponseDTO(AppointmentModel appointment) {
        AppointmentReponseDTO AppointmentDto = new AppointmentReponseDTO();

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

        return AppointmentDto;
    }


}
