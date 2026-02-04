package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.SlotTimes.SlotTimesDTO;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OffDay.OffDaysModel;
import com.wesleysilva.bappoint.OffDay.OffDaysRepository;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursRepository;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursService;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Services.ServiceRepository;
import com.wesleysilva.bappoint.Settings.SettingsDTO;
import com.wesleysilva.bappoint.Settings.SettingsService;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import com.wesleysilva.bappoint.enums.WeekDay;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final CompanyRepository companyRepository;
    private final OperatingHoursService operatingHoursService;
    private final OperatingHoursRepository operatingHoursRepository;
    private final SettingsService settingsService;
    private final OffDaysRepository offDaysRepository;
    private final ServiceRepository serviceRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper, CompanyRepository companyRepository, OperatingHoursService operatingHoursService, OperatingHoursRepository operatingHoursRepository, SettingsService settingsService, OffDaysRepository offDaysRepository, ServiceRepository serviceRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.companyRepository = companyRepository;
        this.operatingHoursService = operatingHoursService;
        this.operatingHoursRepository = operatingHoursRepository;
        this.settingsService = settingsService;
        this.offDaysRepository = offDaysRepository;
        this.serviceRepository = serviceRepository;
    }

    public List<SlotTimesDTO> findAvailableSlots(UUID companyId, String dateParams) {
        LocalDate date = LocalDate.parse(dateParams);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        WeekDay weekday = WeekDay.valueOf(dayOfWeek.name());

        //check if there is off day in the date
        List<OffDaysModel> offDays = offDaysRepository.findByDate(date);
        if(!offDays.isEmpty()) {
            return List.of();
        }

        SettingsDTO settings = settingsService.getByCompanyId(companyId);
        AppointmentInterval appointmentInterval = settings.getAppointment_interval();

        //check if hours of the day is empty means day off
        List<OperatingHoursModel> hours = operatingHoursRepository.findByWeekday(weekday);
        if (hours.isEmpty()) {
            return List.of();
        }

        //check is there is any appointment for the date
        List<AppointmentModel> bookedAppointments = appointmentRepository
                .findByAppointmentDateAndCompanyId(date, companyId);

        OperatingHoursModel operatingHours = hours.getFirst();
        List<SlotTimesDTO> slots = generateSlots(operatingHours, date, appointmentInterval.getMinutes());

        return slots.stream()
                .filter(slot -> !isSlotOccupied(slot, bookedAppointments, date))
                .toList();
    }

    private List<SlotTimesDTO> generateSlots(OperatingHoursModel operatingHours, LocalDate date, int intervalMinutes) {
        LocalDateTime from = date.atTime(operatingHours.getStart_time());
        LocalDateTime to = date.atTime(operatingHours.getEnd_time());

        List<SlotTimesDTO> slots = new ArrayList<>();
        LocalDateTime current = from;

        while (!current.plusMinutes(intervalMinutes).isAfter(to)) {
            LocalDateTime next = current.plusMinutes(intervalMinutes);
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
                    .mapToInt(ServiceModel::getDuration_minutes)
                    .sum();

            LocalDateTime appointmentStart = appointment.getStartTime();
            LocalDateTime appointmentEnd = appointmentStart.plusMinutes(totalDuration);

            return !(slotEnd.isBefore(appointmentStart) || slotStart.isAfter(appointmentEnd));
        });
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
                .map(appointmentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentModel> findByCompanyId(UUID companyId) {
        return appointmentRepository.findByCompanyId(companyId);  // Carrega todos
    }
}
