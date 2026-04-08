package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Services.ServiceRepository;
import com.wesleysilva.bappoint.Settings.SettingsService;
import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private AppointmentMapper appointmentMapper;
    @Mock private CompanyRepository companyRepository;
    @Mock private SettingsService settingsService;
    @Mock private ServiceRepository serviceRepository;
    @Mock private SlotsTimesService slotsTimesService;

    @InjectMocks
    private AppointmentService appointmentService;

    private UUID companyId;
    private UUID serviceId;
    private CreateAppointmentDTO dto;
    private ServiceModel serviceModel;
    private SettingsAllDetailsDTO settings;
    private CompanyModel company;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        UUID settingsId = UUID.randomUUID();

        dto = new CreateAppointmentDTO();
        dto.setStartTime(LocalDateTime.of(2026, 4, 10, 10, 0));
        dto.setServiceIds(List.of(serviceId));
        dto.setCostumerName("Wesley");
        dto.setCostumerEmail("wesley@email.com");
        dto.setCostumerPhone("999999999");
        dto.setStripeSessionId("stripe_session_123");

        serviceModel = new ServiceModel();
        serviceModel.setId(serviceId);
        serviceModel.setDurationMinutes(30);
        serviceModel.setPrice(new BigDecimal("50.00"));

        settings = new SettingsAllDetailsDTO();
        settings.setId(settingsId);

        company = new CompanyModel();
        company.setId(companyId);
    }

    @Test
    @DisplayName("Should create an appointment successfully")
    void shouldCreateAppointmentSuccessfully() {
        LocalDate date = dto.getStartTime().toLocalDate();

        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of()); // none time available

        when(settingsService.getByCompanyId(companyId))
                .thenReturn(settings);

        when(serviceRepository.findByIdAndSettingsId(serviceId, settings.getId()))
                .thenReturn(Optional.of(serviceModel));

        when(slotsTimesService.isRangeWithinSlots(any(), any(), any(), any()))
                .thenReturn(true);

        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));

        AppointmentModel saved = new AppointmentModel();
        saved.setId(UUID.randomUUID());
        when(appointmentRepository.save(any(AppointmentModel.class)))
                .thenReturn(saved);

        CreateAppointmentDTO response = new CreateAppointmentDTO();
        when(appointmentMapper.toCreateAppointmentDTO(saved))
                .thenReturn(response);

        CreateAppointmentDTO result = appointmentService.createAppointment(dto, companyId);

        assertNotNull(result);
        verify(appointmentRepository).save(any(AppointmentModel.class));
    }

    @Test
    @DisplayName("Should throw exception when time slot has conflict")
    void shouldThrowWhenTimeSlotConflicts() {
        LocalDate date = dto.getStartTime().toLocalDate();

        // if booking in the same time as existed booking
        AppointmentModel conflicting = new AppointmentModel();
        conflicting.setStartTime(LocalDateTime.of(2026, 4, 10, 9, 50));
        conflicting.setEndTime(LocalDateTime.of(2026, 4, 10, 10, 20));
        conflicting.setAppointmentStatus(AppointmentStatus.PENDING);

        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of(conflicting));

        when(settingsService.getByCompanyId(companyId))
                .thenReturn(settings);

        when(serviceRepository.findByIdAndSettingsId(serviceId, settings.getId()))
                .thenReturn(Optional.of(serviceModel));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                appointmentService.createAppointment(dto, companyId)
        );

        assertEquals("This slot is already occupied", exception.getMessage());
        verify(appointmentRepository, never()).save(any()); // should not save
    }

    @Test
    @DisplayName("Should throw exception when slot is outside company hours")
    void shouldThrowWhenSlotOutsideCompanyHours() {
        LocalDate date = dto.getStartTime().toLocalDate();

        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of()); // no conflicts

        when(settingsService.getByCompanyId(companyId))
                .thenReturn(settings);

        when(serviceRepository.findByIdAndSettingsId(serviceId, settings.getId()))
                .thenReturn(Optional.of(serviceModel));

        when(slotsTimesService.isRangeWithinSlots(any(), any(), any(), any()))
                .thenReturn(false); // company closed

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                appointmentService.createAppointment(dto, companyId)
        );

        assertEquals("Company closed or invalid slot", exception.getMessage());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when service is not found")
    void shouldThrowWhenServiceNotFound() {
        LocalDate date = dto.getStartTime().toLocalDate();

        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of());

        when(settingsService.getByCompanyId(companyId))
                .thenReturn(settings);

        when(serviceRepository.findByIdAndSettingsId(serviceId, settings.getId()))
                .thenReturn(Optional.empty()); // service not exist

        assertThrows(RuntimeException.class, () ->
                appointmentService.createAppointment(dto, companyId)
        );

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should ignore CANCELLED and NOT_PAID appointments when checking conflict")
    void shouldIgnoreCancelledAndNotPaidWhenCheckingConflict() {
        LocalDate date = dto.getStartTime().toLocalDate();

        // same time, but the invalid status
        AppointmentModel cancelled = new AppointmentModel();
        cancelled.setStartTime(LocalDateTime.of(2026, 4, 10, 9, 50));
        cancelled.setEndTime(LocalDateTime.of(2026, 4, 10, 10, 20));
        cancelled.setAppointmentStatus(AppointmentStatus.CANCELLED);

        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of(cancelled));

        when(settingsService.getByCompanyId(companyId))
                .thenReturn(settings);

        when(serviceRepository.findByIdAndSettingsId(serviceId, settings.getId()))
                .thenReturn(Optional.of(serviceModel));

        when(slotsTimesService.isRangeWithinSlots(any(), any(), any(), any()))
                .thenReturn(true);

        when(companyRepository.findById(companyId))
                .thenReturn(Optional.of(company));

        AppointmentModel saved = new AppointmentModel();
        when(appointmentRepository.save(any())).thenReturn(saved);
        when(appointmentMapper.toCreateAppointmentDTO(saved)).thenReturn(new CreateAppointmentDTO());

        assertDoesNotThrow(() -> appointmentService.createAppointment(dto, companyId));
    }
}
