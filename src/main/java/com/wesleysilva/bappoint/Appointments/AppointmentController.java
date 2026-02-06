package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
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
    private final SlotsTimesService slotsTimesService;

    public AppointmentController(AppointmentService appointmentService, AppointmentMapper appointmentMapper, SlotsTimesService slotsTimesService) {
        this.appointmentService = appointmentService;
        this.appointmentMapper = appointmentMapper;
        this.slotsTimesService = slotsTimesService;
    }

    @GetMapping("/available-times")
    @Operation(
            summary = "",
            description = ""
    )
    public List<SlotTimesDTO> getAvailableTimes(@PathVariable UUID companyId, @RequestParam String date) {
        return slotsTimesService.findAvailableSlots(companyId, date);
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
    public ResponseEntity<List<AppointmentResponseDTO>> listAppointments() {
        List<AppointmentResponseDTO> appointments = appointmentService.listAppointments();

        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/{appointmentId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<AppointmentResponseDTO> listAppointmentById(@PathVariable UUID appointmentId) {
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/update/{appointmentId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<?> updateAppointment(@RequestBody AppointmentResponseDTO appointmentResponseDTO, @PathVariable UUID appointmentId) {
        AppointmentResponseDTO appointment = appointmentService.updateAppointment(appointmentId, appointmentResponseDTO);

        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        } else  {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("delete/{appointmentId}")
    @Operation(
            summary = "",
            description = ""
    )
    void deleteAppointment(UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
    }
}
