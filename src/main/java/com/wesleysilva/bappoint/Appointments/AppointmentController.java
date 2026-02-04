package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.SlotTimes.SlotTimesDTO;
import com.wesleysilva.bappoint.Company.CompanyDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("company/{companyId}/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    public AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
    }

    @GetMapping("/available-times")
    @Operation(
            summary = "",
            description = ""
    )
    public List<SlotTimesDTO> getAvailableTimes(@PathVariable UUID companyId, @RequestParam String date) {
        return appointmentService.findAvailableSlots(companyId, date);
    }

    @PostMapping("/create")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<AppointmentResponseDTO> createAppointment(@PathVariable UUID companyId, @RequestBody AppointmentDTO appointmentDTO) {

        AppointmentModel appointment = appointmentService.createAppointment(appointmentDTO, companyId);

        return ResponseEntity.ok(appointmentMapper.toResponseDTO(appointment));
    }

    @GetMapping("/list")
    @Transactional(readOnly = true)
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<List<AppointmentResponseDTO>> listAppointments(@PathVariable UUID companyId) {
        List<AppointmentResponseDTO> appointments = appointmentService.findByCompanyId(companyId).stream()
                .map(appointmentMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }
}
