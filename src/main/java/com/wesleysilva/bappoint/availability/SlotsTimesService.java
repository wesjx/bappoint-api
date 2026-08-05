package com.wesleysilva.bappoint.availability;

import com.wesleysilva.bappoint.appointments.AppointmentModel;
import com.wesleysilva.bappoint.appointments.AppointmentRepository;
import com.wesleysilva.bappoint.offday.OffDaysModel;
import com.wesleysilva.bappoint.offday.OffDaysRepository;
import com.wesleysilva.bappoint.operatinghours.OperatingHoursModel;
import com.wesleysilva.bappoint.operatinghours.OperatingHoursRepository;
import com.wesleysilva.bappoint.services.ServiceModel;
import com.wesleysilva.bappoint.settings.SettingsService;
import com.wesleysilva.bappoint.settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import com.wesleysilva.bappoint.enums.WeekDay;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SlotsTimesService {
    private final AppointmentRepository appointmentRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final SettingsService settingsService;
    private final OffDaysRepository offDaysRepository;

    public SlotsTimesService(AppointmentRepository appointmentRepository, OperatingHoursRepository operatingHoursRepository, SettingsService settingsService, OffDaysRepository offDaysRepository) {
        this.appointmentRepository = appointmentRepository;
        this.operatingHoursRepository = operatingHoursRepository;
        this.settingsService = settingsService;
        this.offDaysRepository = offDaysRepository;
    }

    public List<SlotTimesDTO> findAvailableSlots(UUID companyId, String dateParams) {
        LocalDate date = LocalDate.parse(dateParams);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        WeekDay weekday = WeekDay.valueOf(dayOfWeek.name());

        //check if there is off day in the date
        List<OffDaysModel> offDays = offDaysRepository.findBySettingsCompanyIdAndDate(companyId, date);
        if (!offDays.isEmpty()) {
            return List.of();
        }

        SettingsAllDetailsDTO settings = settingsService.getByCompanyId(companyId);
        AppointmentInterval appointmentInterval = settings.getAppointmentInterval();

        //check if hours of the day is empty means day off
        OperatingHoursModel operatingHours = operatingHoursRepository
                .findBySettingsCompanyIdAndWeekday(companyId, weekday)
                .orElse(null);

        if (operatingHours == null) {
            return List.of();
        }

        if (Boolean.FALSE.equals(operatingHours.getIsActive())) {
            return List.of();
        }

        //check is there is any appointment for the date
        List<AppointmentModel> bookedAppointments = appointmentRepository
                .findByAppointmentDateAndCompanyId(date, companyId)
                .stream()
                .filter(a -> a.getAppointmentStatus() != AppointmentStatus.NOT_PAID
                        && a.getAppointmentStatus() != AppointmentStatus.CANCELLED)
                .toList();

        List<SlotTimesDTO> slots = generateSlots(operatingHours, date, appointmentInterval.getMinutes());

        return slots.stream()
                .filter(slot -> !isSlotOccupied(slot, bookedAppointments, date))
                .toList();
    }

    private List<SlotTimesDTO> generateSlots(OperatingHoursModel operatingHours, LocalDate date, int intervalMinutes) {
        LocalDateTime from = date.atTime(operatingHours.getStartTime());
        LocalDateTime to = date.atTime(operatingHours.getEndTime());

        LocalTime lunchStart = operatingHours.getLunchStartTime();
        LocalTime lunchEnd = operatingHours.getLunchEndTime();

        List<SlotTimesDTO> slots = new ArrayList<>();
        LocalDateTime current = from;

        while (!current.plusMinutes(intervalMinutes).isAfter(to)) {
            LocalDateTime next = current.plusMinutes(intervalMinutes);

            //skip lunchtime
            if (lunchStart != null && lunchEnd != null) {
                LocalDateTime lunchFrom = date.atTime(lunchStart);
                LocalDateTime lunchTo = date.atTime(lunchEnd);

                boolean intersectsLunch =
                        current.isBefore(lunchTo) && next.isAfter(lunchFrom);

                if (intersectsLunch) {
                    current = lunchTo; //go to end of lunch
                    continue;
                }
            }

            slots.add(new SlotTimesDTO(current.toString(), next.toString()));
            current = next;
        }
        return slots;
    }

    //check if the slot is occupied
    private boolean isSlotOccupied(SlotTimesDTO slot, List<AppointmentModel> booked, LocalDate date) {
        LocalDateTime slotStart = LocalDateTime.parse(slot.getStart());
        LocalDateTime slotEnd = LocalDateTime.parse(slot.getEnd());

        return booked.stream().anyMatch(appointment -> {
            if (appointment.getServices() == null || appointment.getServices().isEmpty()) {
                return false;
            }

            int totalDuration = appointment.getServices().stream()
                    .mapToInt(ServiceModel::getDurationMinutes)
                    .sum();

            LocalDateTime appointmentStart = appointment.getStartTime();
            LocalDateTime appointmentEnd = appointmentStart.plusMinutes(totalDuration);

            return !(slotEnd.isBefore(appointmentStart) || slotStart.isAfter(appointmentEnd));
        });
    }

    public boolean isRangeWithinSlots(UUID companyId, LocalDate date, LocalDateTime start, LocalDateTime end) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        WeekDay weekday = WeekDay.valueOf(dayOfWeek.name());

        if (!offDaysRepository.findByDate(date).isEmpty()) return false;

        SettingsAllDetailsDTO settings = settingsService.getByCompanyId(companyId);
        int intervalMinutes = settings.getAppointmentInterval().getMinutes();

        List<OperatingHoursModel> hours = operatingHoursRepository
                .findByWeekdayAndSettingsId(weekday, settings.getId());
        if (hours.isEmpty()) return false;

        OperatingHoursModel oh = hours.getFirst();
        LocalDateTime dayStart = date.atTime(oh.getStartTime());
        LocalDateTime dayEnd = date.atTime(oh.getEndTime());

        long startMinutes = ChronoUnit.MINUTES.between(dayStart, start);
        if (startMinutes % intervalMinutes != 0 || start.isBefore(dayStart) || start.isAfter(dayEnd)) {
            return false;
        }

        if (end.isAfter(dayEnd)) return false;

        LocalTime lunchStart = oh.getLunchStartTime();
        LocalTime lunchEnd = oh.getLunchEndTime();
        if (lunchStart != null && lunchEnd != null) {
            LocalDateTime lunchFrom = date.atTime(lunchStart);
            LocalDateTime lunchTo = date.atTime(lunchEnd);
            return !start.isBefore(lunchTo) || !end.isAfter(lunchFrom);
        }

        return true;
    }
}
