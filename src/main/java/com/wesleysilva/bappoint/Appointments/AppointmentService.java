package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Services.ServiceRepository;
import com.wesleysilva.bappoint.Settings.SettingsService;
import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import com.wesleysilva.bappoint.exceptions.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
    private final SlotsTimesService slotsTimesService;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, CompanyRepository companyRepository, SettingsService settingsService, ServiceRepository serviceRepository, SlotsTimesService slotsTimesService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.companyRepository = companyRepository;
        this.settingsService = settingsService;
        this.serviceRepository = serviceRepository;
        this.slotsTimesService = slotsTimesService;
    }

    public CreateAppointmentDTO createAppointment(CreateAppointmentDTO appointmentDTO, UUID companyId) {

        LocalDate date = appointmentDTO.getStartTime().toLocalDate();

        List<AppointmentModel> booked = appointmentRepository
                .findByAppointmentDateAndCompanyId(date, companyId)
                .stream()
                .filter(a -> a.getAppointmentStatus() != AppointmentStatus.NOT_PAID
                        && a.getAppointmentStatus() != AppointmentStatus.CANCELLED)
                .toList();

        SettingsAllDetailsDTO settings = settingsService.getByCompanyId(companyId);
        UUID settingsId = settings.getId();

        List<ServiceModel> services = appointmentDTO.getServiceIds().stream()
                .map(id -> serviceRepository.findByIdAndSettingsId(id, settingsId)
                        .orElseThrow(() -> new RuntimeException("Service not found")))
                .toList();

        int totalDuration = services.stream()
                .mapToInt(ServiceModel::getDurationMinutes)
                .sum();

        LocalDateTime start = appointmentDTO.getStartTime();
        LocalDateTime end = start.plusMinutes(totalDuration);

        boolean hasConflict = booked.stream().anyMatch(existing ->
                start.isBefore(existing.getEndTime()) &&
                        end.isAfter(existing.getStartTime())
        );

        if (hasConflict) {
            throw new IllegalStateException("This slot is already occupied");
        }

        boolean allowed = slotsTimesService.isRangeWithinSlots(companyId, date, start, end);
        if (!allowed) {
            throw new IllegalStateException("Company closed or invalid slot");
        }

        BigDecimal totalAmount = services.stream()
                .map(ServiceModel::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        AppointmentModel appointment = new AppointmentModel();
        appointment.setCompany(
                companyRepository.findById(companyId)
                        .orElseThrow(CompanyNotFoundException::new)
        );

        appointment.setAppointmentDate(date);
        appointment.setStartTime(start);
        appointment.setEndTime(end);
        appointment.setServices(services);
        appointment.setCostumerName(appointmentDTO.getCostumerName());
        appointment.setCostumerEmail(appointmentDTO.getCostumerEmail());
        appointment.setCostumerPhone(appointmentDTO.getCostumerPhone());
        appointment.setTotalAmount(totalAmount);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointment.setStripeSessionId(appointmentDTO.getStripeSessionId());
        appointment.setPaymentDeadline(LocalDateTime.now().plusMinutes(10));
        appointment.setCreatedAt(LocalDateTime.now());

        AppointmentModel savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toCreateAppointmentDTO(savedAppointment);
    }


    public List<AppointmentAllDetailsDTO> listAppointments(int page, int itemsPerPage) {
        Page<AppointmentModel> appointments = appointmentRepository.findAll(PageRequest.of(page, itemsPerPage) );
        return appointments.stream()
                .map(appointmentMapper::toResponseAllDetailsDTO)
                .collect(Collectors.toList());
    }

    public AppointmentAllDetailsDTO getAppointmentById(UUID appointmentId) {
        AppointmentModel appointment = appointmentRepository.findById(appointmentId).orElseThrow(AppointmentNotFoundException::new);

        return appointmentMapper.toResponseAllDetailsDTO(appointment);
    }

    void deleteAppointment(UUID appointmentId) {
        AppointmentModel appointment = appointmentRepository.findById(appointmentId).orElseThrow(AppointmentNotFoundException::new);

        try{
            appointmentRepository.delete(appointment);
        } catch (java.lang.Exception exception){
            throw new AppointmentNotFoundException();
        }

    }

    public UpdateAppointmentDTO updateAppointment(UUID appointmentId, UpdateAppointmentDTO appointmentDto) {
        Optional<AppointmentModel> existingAppointment = Optional.of(appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new));

        try {
            List<ServiceModel> services = appointmentDto.getServiceIds().stream()
                    .map(serviceId -> serviceRepository.findById(serviceId)
                            .orElseThrow(ServiceNotFoundException::new))
                    .toList();

            AppointmentModel appointmentToUpdate = existingAppointment.get();

            appointmentToUpdate.setAppointmentDate(appointmentDto.getAppointmentDate());
            appointmentToUpdate.setStartTime(appointmentDto.getStartTime());
            appointmentToUpdate.setEndTime(appointmentDto.getEndTime());
            appointmentToUpdate.setServices(services);

            appointmentToUpdate.setCostumerName(appointmentDto.getCostumerName());
            appointmentToUpdate.setCostumerEmail(appointmentDto.getCostumerEmail());
            appointmentToUpdate.setCostumerPhone(appointmentDto.getCostumerPhone());
            appointmentToUpdate.setTotalAmount(appointmentDto.getTotalAmount());
            appointmentToUpdate.setAppointmentStatus(appointmentDto.getAppointmentStatus());
            appointmentToUpdate.setStripeSessionId(appointmentDto.getStripeSessionId());

            return appointmentMapper.toUpdateAppointmentDTO(appointmentRepository.save(appointmentToUpdate));
        } catch (Exception exception){
            throw new AppointmentUpdateException();
        }

    }

    @Transactional(readOnly = true)
    public List<AppointmentReponseDTO> listAppointmentsByDate(LocalDate date, UUID companyId) {
        companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        try {
            List<AppointmentModel> appointments = appointmentRepository
                    .findByAppointmentDateAndCompanyId(date, companyId);

            return appointments.stream()
                    .map(appointmentMapper::toResponseDTO)
                    .toList();

        } catch (DataAccessException e) {
            throw new AppointmentQueryException();
        }
    }


}
