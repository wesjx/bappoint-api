package com.wesleysilva.bappoint.appointments;

import com.wesleysilva.bappoint.appointments.dto.*;
import com.wesleysilva.bappoint.appointments.records.ServiceDetailsResult;
import com.wesleysilva.bappoint.availability.SlotsTimesService;
import com.wesleysilva.bappoint.company.CompanyRepository;
import com.wesleysilva.bappoint.services.ServiceModel;
import com.wesleysilva.bappoint.services.ServiceRepository;
import com.wesleysilva.bappoint.settings.SettingsService;
import com.wesleysilva.bappoint.settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import com.wesleysilva.bappoint.exceptions.*;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    private List<ServiceModel> getServicesByIds(List<UUID> serviceIds, UUID settingsId) {
        return serviceIds.stream()
                .map(id -> serviceRepository.findByIdAndSettingsId(id, settingsId)
                        .orElseThrow(() -> new RuntimeException("Service not found: " + id)))
                .toList();
    }

    private int calculateTotalDuration(List<ServiceModel> services) {
        return services.stream()
                .mapToInt(ServiceModel::getDurationMinutes)
                .sum();
    }

    private AppointmentModel findAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }


    @Transactional
    public CreateAppointmentDTO createAppointment(CreateAppointmentDTO appointmentDTO, UUID companyId) {
        try {
            LocalDate date = appointmentDTO.getStartTime().toLocalDate();

            List<AppointmentModel> booked = appointmentRepository
                    .findByAppointmentDateAndCompanyId(date, companyId)
                    .stream()
                    .filter(a -> a.getAppointmentStatus() != AppointmentStatus.NOT_PAID
                            && a.getAppointmentStatus() != AppointmentStatus.CANCELLED)
                    .toList();

            SettingsAllDetailsDTO settings = settingsService.getByCompanyId(companyId);
            UUID settingsId = settings.getId();

            List<ServiceModel> services = getServicesByIds(appointmentDTO.getServiceIds(), settingsId);
            int totalDuration = calculateTotalDuration(services);

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

            String slotKey = companyId + "|" + date + "|" + start;

            AppointmentModel appointment = new AppointmentModel();
            appointment.setCompany(
                    companyRepository.findById(companyId)
                            .orElseThrow(CompanyNotFoundException::new)
            );

            appointment.setSlotKey(slotKey);
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

            AppointmentModel savedAppointment = appointmentRepository.saveAndFlush(appointment);
            return appointmentMapper.toCreateAppointmentDTO(savedAppointment);

        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalStateException("This slot is already occupied");
        }
    }


    public Page<AppointmentAllDetailsDTO> listAppointments(UUID companyId, int page, int itemsPerPage, String search, AppointmentStatus status) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(itemsPerPage, 1),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Specification<AppointmentModel> specification =
                Specification.where(AppointmentSpecifications.belongsToCompany(companyId))
                        .and(AppointmentSpecifications.searchByCustomer(search))
                        .and(AppointmentSpecifications.hasStatus(status));
        return appointmentRepository.findAll(specification, pageable)
                .map(appointmentMapper::toResponseAllDetailsDTO);
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
    public List<AppointmentResponseDTO> listAppointmentsByDate(LocalDate date, UUID companyId) {
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

    @Transactional
    public AppointmentResponseDTO rescheduleAppointment(UUID appointmentId, RescheduleAppointmentDTO dto) {
        AppointmentModel appointment = findAppointmentById(appointmentId);

        int totalDuration = calculateTotalDuration(appointment.getServices());

        LocalDateTime newStartTime = dto.getStartTime();
        LocalDateTime newEndTime = newStartTime.plusMinutes(totalDuration);

        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStartTime(newStartTime);
        appointment.setEndTime(newEndTime);

        return appointmentMapper.toResponseDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public UpdateAppointmentStatusDTO updateAppointmentStatus(UUID appointmentId, UpdateAppointmentStatusDTO dto) {
        AppointmentModel appointment = findAppointmentById(appointmentId);
        AppointmentStatus currentStatus = appointment.getAppointmentStatus();
        AppointmentStatus newStatus = dto.getStatus();

        if (currentStatus == AppointmentStatus.COMPLETED && newStatus == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Completed appointment cannot be cancelled.");
        }

        if (currentStatus == AppointmentStatus.CANCELLED && newStatus == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cancelled appointment cannot be completed.");
        }

        appointment.setAppointmentStatus(newStatus);

        return appointmentMapper.toUpdateAppointmentStatusDTO(
                appointmentRepository.save(appointment)
        );
    }


}
