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
        AppointmentAllDetailsDTO appointmentDto = new AppointmentAllDetailsDTO();
        appointmentDto.setId(appointment.getId());
        appointmentDto.setCostumerName(appointment.getCostumerName());
        appointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        appointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        appointmentDto.setStartTime(appointment.getStartTime());
        appointmentDto.setEndTime(appointment.getEndTime());
        appointmentDto.setTotalAmount(appointment.getTotalAmount());
        appointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        appointmentDto.setStripeSessionId(appointment.getStripeSessionId());
        appointmentDto.setCreatedAt(appointment.getCreatedAt());

        appointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        appointmentDto.setCompanyId(appointment.getCompany().getId());
        return appointmentDto;
    }

    public CreateAppointmentDTO toCreateAppointmentDTO(AppointmentModel appointment) {
        CreateAppointmentDTO appointmentDto = new CreateAppointmentDTO();

        appointmentDto.setCostumerName(appointment.getCostumerName());
        appointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        appointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        appointmentDto.setStartTime(appointment.getStartTime());
        appointmentDto.setEndTime(appointment.getEndTime());
        appointmentDto.setTotalAmount(appointment.getTotalAmount());
        appointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        appointmentDto.setStripeSessionId(appointment.getStripeSessionId());
        appointmentDto.setCreatedAt(appointment.getCreatedAt());
        appointmentDto.setPaymentDeadline(appointment.getPaymentDeadline());

        appointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        appointmentDto.setCompanyId(appointment.getCompany().getId());
        return appointmentDto;
    }

    public UpdateAppointmentDTO toUpdateAppointmentDTO(AppointmentModel appointment) {
        UpdateAppointmentDTO appointmentDto = new UpdateAppointmentDTO();

        appointmentDto.setCostumerName(appointment.getCostumerName());
        appointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        appointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        appointmentDto.setStartTime(appointment.getStartTime());
        appointmentDto.setEndTime(appointment.getEndTime());
        appointmentDto.setTotalAmount(appointment.getTotalAmount());
        appointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());
        appointmentDto.setStripeSessionId(appointment.getStripeSessionId());

        appointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        return appointmentDto;
    }

    public AppointmentReponseDTO toResponseDTO(AppointmentModel appointment) {
        AppointmentReponseDTO appointmentDto = new AppointmentReponseDTO();

        appointmentDto.setCostumerName(appointment.getCostumerName());
        appointmentDto.setCostumerEmail(appointment.getCostumerEmail());
        appointmentDto.setCostumerPhone(appointment.getCostumerPhone());
        appointmentDto.setAppointmentDate(appointment.getAppointmentDate());
        appointmentDto.setStartTime(appointment.getStartTime());
        appointmentDto.setEndTime(appointment.getEndTime());
        appointmentDto.setTotalAmount(appointment.getTotalAmount());
        appointmentDto.setAppointmentStatus(appointment.getAppointmentStatus());

        appointmentDto.setServiceIds(Optional.ofNullable(appointment.getServices())
                .map(services -> services.stream()
                        .map(ServiceModel::getId)
                        .toList())
                .orElse(List.of()));

        return appointmentDto;
    }


}
