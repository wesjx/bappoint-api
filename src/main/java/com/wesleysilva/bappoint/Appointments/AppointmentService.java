package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Services.ServiceRepository;
import com.wesleysilva.bappoint.Settings.SettingsDTO;
import com.wesleysilva.bappoint.Settings.SettingsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final CompanyRepository companyRepository;
    private final SettingsService settingsService;
    private final ServiceRepository serviceRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, CompanyRepository companyRepository, SettingsService settingsService, ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.companyRepository = companyRepository;
        this.settingsService = settingsService;
        this.serviceRepository = serviceRepository;
    }

    public AppointmentModel createAppointment(AppointmentDTO dto, UUID companyId) {

        LocalDate date = dto.getStartTime().toLocalDate();

        List<AppointmentModel> booked = appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId);

        SettingsDTO settings = settingsService.getByCompanyId(companyId);
        UUID settingsId = settings.getId();

        List<ServiceModel> services = dto.getServiceIds().stream()
                .map(id -> serviceRepository.findByIdAndSettingsId(id, settingsId)
                        .orElseThrow(() -> new RuntimeException("Service not found")))
                .toList();

        int totalDuration = services.stream()
                .mapToInt(ServiceModel::getDuration_minutes)
                .sum();

        LocalDateTime start = dto.getStartTime();
        LocalDateTime end = start.plusMinutes(totalDuration);

        boolean hasConflict = booked.stream().anyMatch(existing ->
                start.isBefore(existing.getEndTime()) &&
                        end.isAfter(existing.getStartTime())
        );

        if (hasConflict) {
            throw new IllegalStateException("This slot is already occupied");
        }

        double totalAmount = services.stream()
                .mapToDouble(ServiceModel::getPrice)
                .sum();

        AppointmentModel appointment = new AppointmentModel();
        appointment.setCompany(
                companyRepository.findById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company not found"))
        );

        appointment.setAppointmentDate(date);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setServices(services);
        appointment.setCostumerName(dto.getCostumerName());
        appointment.setCostumerEmail(dto.getCostumerEmail());
        appointment.setCostumerPhone(dto.getCostumerPhone());
        appointment.setTotalAmount(totalAmount);
        appointment.setAppointmentStatus(dto.getAppointmentStatus());

        return appointmentRepository.save(appointment);
    }


    public List<AppointmentDTO> listAppointments() {
        List<AppointmentModel> appointments = appointmentRepository.findAll();
        return appointments.stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO getAppointmentById(UUID appointmentId) {
        AppointmentModel appointment = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new RuntimeException("Appointment not found"));

        return appointmentMapper.toResponseDTO(appointment);
    }

    void deleteAppointment(UUID appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    public AppointmentDTO updateAppointment(UUID appointmentId, AppointmentDTO appointmentDto) {
        Optional<AppointmentModel> existingAppointment = appointmentRepository.findById(appointmentId);
        if (existingAppointment.isPresent()) {
            AppointmentModel appointmentToUpdate = existingAppointment.get();

            appointmentToUpdate.setAppointmentDate(appointmentDto.getAppointmentDate());
            appointmentToUpdate.setStartTime(appointmentDto.getStartTime());
            appointmentToUpdate.setEndTime(appointmentDto.getEndTime());

            List<ServiceModel> services = appointmentDto.getServiceIds().stream()
                    .map(serviceId -> serviceRepository.findById(serviceId)
                            .orElseThrow(() -> new RuntimeException("Service not found: " + serviceId)))
                    .collect(Collectors.toList());
            appointmentToUpdate.setServices(services);

            appointmentToUpdate.setCostumerName(appointmentDto.getCostumerName());
            appointmentToUpdate.setCostumerEmail(appointmentDto.getCostumerEmail());
            appointmentToUpdate.setCostumerPhone(appointmentDto.getCostumerPhone());
            appointmentToUpdate.setTotalAmount(appointmentDto.getTotalAmount());
            appointmentToUpdate.setAppointmentStatus(appointmentDto.getAppointmentStatus());

            return appointmentMapper.toResponseDTO(appointmentRepository.save(appointmentToUpdate));
        }
        return null;

    }

    @Transactional(readOnly = true)
    public List<AppointmentModel> findByCompanyId(UUID companyId) {
        return appointmentRepository.findByCompanyId(companyId);
    }


}
