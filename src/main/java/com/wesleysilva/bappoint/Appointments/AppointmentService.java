package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.SlotTimes.SlotTimesDTO;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursRepository;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursService;
import com.wesleysilva.bappoint.Settings.SettingsDTO;
import com.wesleysilva.bappoint.Settings.SettingsService;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import com.wesleysilva.bappoint.enums.WeekDay;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CompanyRepository companyRepository;
    private final OperatingHoursService operatingHoursService;
    private final OperatingHoursRepository operatingHoursRepository;
    private final SettingsService settingsService;

    public AppointmentService(AppointmentRepository appointmentRepository, CompanyRepository companyRepository, OperatingHoursService operatingHoursService, OperatingHoursRepository operatingHoursRepository, SettingsService settingsService) {
        this.appointmentRepository = appointmentRepository;
        this.companyRepository = companyRepository;
        this.operatingHoursService = operatingHoursService;
        this.operatingHoursRepository = operatingHoursRepository;
        this.settingsService = settingsService;
    }

    public List<SlotTimesDTO> findAvailableSlots(UUID companyId, String dateParams) {

        SettingsDTO settings = settingsService.getByCompanyId(companyId);

        AppointmentInterval appointmentInterval = settings.getAppointment_interval();
        LocalDate date = LocalDate.parse(dateParams);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        WeekDay weekday = WeekDay.valueOf(dayOfWeek.name());

        List<OperatingHoursModel> hours = operatingHoursRepository
                .findByWeekday(weekday);

        if (hours.isEmpty()) {
            return List.of();
        }

        OperatingHoursModel operatingHours = hours.getFirst();

        LocalDateTime from = operatingHours.getStart_date();
        LocalDateTime to = operatingHours.getEnd_date();

        List<SlotTimesDTO> slots = new ArrayList<>();
        LocalDateTime current = from;

        while (current.isBefore(to)) {
            LocalDateTime next = current.plusMinutes(appointmentInterval.getMinutes());
            slots.add(new SlotTimesDTO(current.toString(), next.toString()));
            current = next;
        }

        return slots;
    }

}
