package com.wesleysilva.bappoint.Availability;

import com.wesleysilva.bappoint.Appointments.AppointmentModel;
import com.wesleysilva.bappoint.Appointments.AppointmentRepository;
import com.wesleysilva.bappoint.OffDay.OffDaysModel;
import com.wesleysilva.bappoint.OffDay.OffDaysRepository;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursRepository;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Settings.SettingsService;
import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import com.wesleysilva.bappoint.enums.WeekDay;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotsTimesServiceTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private OperatingHoursRepository operatingHoursRepository;
    @Mock private SettingsService settingsService;
    @Mock private OffDaysRepository offDaysRepository;

    @InjectMocks
    private SlotsTimesService slotsTimesService;

    private UUID companyId;
    private UUID settingsId;
    private SettingsAllDetailsDTO settings;
    private OperatingHoursModel operatingHours;

    // April 9, 2026 is a THURSDAY — matches WeekDay.THURSDAY
    private final LocalDate date = LocalDate.of(2026, 4, 9);
    private final String dateStr = "2026-04-09";

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        settingsId = UUID.randomUUID();

        settings = new SettingsAllDetailsDTO();
        settings.setId(settingsId);
        settings.setAppointmentInterval(AppointmentInterval.MINUTES_30); // 30 min

        // operating hours: 08:00 - 18:00, no lunch break
        operatingHours = new OperatingHoursModel();
        operatingHours.setWeekday(WeekDay.THURSDAY);
        operatingHours.setStartTime(LocalTime.of(8, 0));
        operatingHours.setEndTime(LocalTime.of(18, 0));
        operatingHours.setLunchStartTime(null);
        operatingHours.setLunchEndTime(null);
    }

    // -------------------------------------------------------------------------
    // findAvailableSlots
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return empty list when date is an off day")
    void findAvailableSlots_shouldReturnEmptyWhenOffDay() {
        when(offDaysRepository.findByDate(date))
                .thenReturn(List.of(new OffDaysModel()));

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        assertTrue(result.isEmpty());
        verifyNoInteractions(operatingHoursRepository, appointmentRepository, settingsService);
    }

    @Test
    @DisplayName("Should return empty list when no operating hours exist for the day")
    void findAvailableSlots_shouldReturnEmptyWhenNoOperatingHours() {
        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekday(WeekDay.THURSDAY)).thenReturn(List.of());

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        assertTrue(result.isEmpty());
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    @DisplayName("Should return all slots when no appointments are booked")
    void findAvailableSlots_shouldReturnAllSlotsWhenNoneBooked() {
        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekday(WeekDay.THURSDAY))
                .thenReturn(List.of(operatingHours));
        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of());

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        // 08:00 to 18:00 with 30-min intervals = 20 slots
        assertFalse(result.isEmpty());
        assertEquals(20, result.size());
    }

    @Test
    @DisplayName("Should exclude slot occupied by a PENDING appointment")
    void findAvailableSlots_shouldExcludeOccupiedSlot() {
        ServiceModel service = new ServiceModel();
        service.setDurationMinutes(30);

        AppointmentModel booked = new AppointmentModel();
        booked.setStartTime(LocalDateTime.of(2026, 4, 9, 9, 0));
        booked.setAppointmentStatus(AppointmentStatus.PENDING);
        booked.setServices(List.of(service));

        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekday(WeekDay.THURSDAY))
                .thenReturn(List.of(operatingHours));
        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of(booked));

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        // 20 total slots minus 1 occupied = 19
        assertEquals(19, result.size());

        boolean slotPresent = result.stream()
                .anyMatch(s -> s.getStart().contains("09:00"));
        assertFalse(slotPresent);
    }

    @Test
    @DisplayName("Should include slot when appointment is CANCELLED")
    void findAvailableSlots_shouldIncludeSlotWhenAppointmentCancelled() {
        ServiceModel service = new ServiceModel();
        service.setDurationMinutes(30);

        AppointmentModel cancelled = new AppointmentModel();
        cancelled.setStartTime(LocalDateTime.of(2026, 4, 9, 9, 0));
        cancelled.setAppointmentStatus(AppointmentStatus.CANCELLED);
        cancelled.setServices(List.of(service));

        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekday(WeekDay.THURSDAY))
                .thenReturn(List.of(operatingHours));
        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of(cancelled));

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        // CANCELLED is ignored — all 20 slots must be available
        assertEquals(20, result.size());
    }

    @Test
    @DisplayName("Should include slot when appointment is NOT_PAID")
    void findAvailableSlots_shouldIncludeSlotWhenAppointmentNotPaid() {
        ServiceModel service = new ServiceModel();
        service.setDurationMinutes(30);

        AppointmentModel notPaid = new AppointmentModel();
        notPaid.setStartTime(LocalDateTime.of(2026, 4, 9, 9, 0));
        notPaid.setAppointmentStatus(AppointmentStatus.NOT_PAID);
        notPaid.setServices(List.of(service));

        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekday(WeekDay.THURSDAY))
                .thenReturn(List.of(operatingHours));
        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of(notPaid));

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        // NOT_PAID is ignored — all 20 slots must be available
        assertEquals(20, result.size());
    }

    @Test
    @DisplayName("Should skip lunch break slots when lunch is configured")
    void findAvailableSlots_shouldSkipLunchBreakSlots() {
        operatingHours.setLunchStartTime(LocalTime.of(12, 0));
        operatingHours.setLunchEndTime(LocalTime.of(13, 0));

        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekday(WeekDay.THURSDAY))
                .thenReturn(List.of(operatingHours));
        when(appointmentRepository.findByAppointmentDateAndCompanyId(date, companyId))
                .thenReturn(List.of());

        List<SlotTimesDTO> result = slotsTimesService.findAvailableSlots(companyId, dateStr);

        // 20 slots minus 2 lunch slots (12:00 and 12:30) = 18
        assertEquals(18, result.size());

        boolean lunchSlotPresent = result.stream()
                .anyMatch(s -> s.getStart().contains("12:00") || s.getStart().contains("12:30"));
        assertFalse(lunchSlotPresent);
    }

    // -------------------------------------------------------------------------
    // isRangeWithinSlots
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return false when date is an off day")
    void isRangeWithinSlots_shouldReturnFalseOnOffDay() {
        when(offDaysRepository.findByDate(date))
                .thenReturn(List.of(new OffDaysModel()));

        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(9, 0),
                date.atTime(9, 30)
        );

        assertFalse(result);
        verifyNoInteractions(settingsService, operatingHoursRepository);
    }

    @Test
    @DisplayName("Should return false when no operating hours exist for the day")
    void isRangeWithinSlots_shouldReturnFalseWhenNoOperatingHours() {
        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekdayAndSettingsId(WeekDay.THURSDAY, settingsId))
                .thenReturn(List.of());

        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(9, 0),
                date.atTime(9, 30)
        );

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when range is valid and aligned to interval")
    void isRangeWithinSlots_shouldReturnTrueForValidRange() {
        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekdayAndSettingsId(WeekDay.THURSDAY, settingsId))
                .thenReturn(List.of(operatingHours));

        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(9, 0),
                date.atTime(9, 30)
        );

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when start time is not aligned to interval")
    void isRangeWithinSlots_shouldReturnFalseWhenNotAligned() {
        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekdayAndSettingsId(WeekDay.THURSDAY, settingsId))
                .thenReturn(List.of(operatingHours));

        // 09:15 is not aligned to 30-min interval starting from 08:00
        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(9, 15),
                date.atTime(9, 45)
        );

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when end time exceeds operating hours")
    void isRangeWithinSlots_shouldReturnFalseWhenEndExceedsHours() {
        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekdayAndSettingsId(WeekDay.THURSDAY, settingsId))
                .thenReturn(List.of(operatingHours));

        // end at 18:30 but company closes at 18:00
        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(17, 30),
                date.atTime(18, 30)
        );

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when range overlaps lunch break")
    void isRangeWithinSlots_shouldReturnFalseWhenOverlapsLunch() {
        operatingHours.setLunchStartTime(LocalTime.of(12, 0));
        operatingHours.setLunchEndTime(LocalTime.of(13, 0));

        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekdayAndSettingsId(WeekDay.THURSDAY, settingsId))
                .thenReturn(List.of(operatingHours));

        // 11:30 to 12:00 overlaps lunch
        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(11, 30),
                date.atTime(12, 0)
        );

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when range is after lunch break")
    void isRangeWithinSlots_shouldReturnTrueWhenAfterLunch() {
        operatingHours.setLunchStartTime(LocalTime.of(12, 0));
        operatingHours.setLunchEndTime(LocalTime.of(13, 0));

        when(offDaysRepository.findByDate(date)).thenReturn(List.of());
        when(settingsService.getByCompanyId(companyId)).thenReturn(settings);
        when(operatingHoursRepository.findByWeekdayAndSettingsId(WeekDay.THURSDAY, settingsId))
                .thenReturn(List.of(operatingHours));

        // 13:00 to 13:30 is after lunch — valid
        boolean result = slotsTimesService.isRangeWithinSlots(
                companyId, date,
                date.atTime(13, 0),
                date.atTime(13, 30)
        );

        assertTrue(result);
    }
}
