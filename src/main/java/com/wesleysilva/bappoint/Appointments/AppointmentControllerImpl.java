package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.doc.AppointmentController;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/appointments")
@Tag(name = "dev/Appointments", description = "Manage appointment scheduling")
public class AppointmentControllerImpl implements AppointmentController {

    private final AppointmentService appointmentService;
    private final SlotsTimesService slotsTimesService;


    public AppointmentControllerImpl(AppointmentService appointmentService, SlotsTimesService slotsTimesService) {
        this.appointmentService = appointmentService;
        this.slotsTimesService = slotsTimesService;
    }



    @GetMapping("/available-times")
    public ResponseEntity<List<SlotTimesDTO>> getAvailableTimes(@PathVariable UUID companyId, @RequestParam String date) {
        List<SlotTimesDTO> slotTimes = slotsTimesService.findAvailableSlots(companyId, date);
        return ResponseEntity.ok(slotTimes);
    }




    @GetMapping("/list")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AppointmentReponseDTO>> listAppointments(@RequestParam int page, int itemsPerPage) {
        List<AppointmentReponseDTO> appointments = appointmentService.listAppointments(page, itemsPerPage);

        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }




    @GetMapping("/{appointmentId}")
    public ResponseEntity<AppointmentAllDetailsDTO> listAppointmentById(@PathVariable UUID appointmentId) {
        AppointmentAllDetailsDTO appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }




    @PostMapping("/create")
    public ResponseEntity<CreateAppointmentDTO> createAppointment(@PathVariable UUID companyId, @RequestBody CreateAppointmentDTO appointmentDTO) {

        CreateAppointmentDTO appointment = appointmentService.createAppointment(appointmentDTO, companyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }




    @PutMapping("/update/{appointmentId}")
    public ResponseEntity<?> updateAppointment(@RequestBody UpdateAppointmentDTO appointmentResponseDTO, @PathVariable UUID appointmentId) {
        UpdateAppointmentDTO appointment = appointmentService.updateAppointment(appointmentId, appointmentResponseDTO);

        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }




    @DeleteMapping("/delete/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }




    @GetMapping("/by-date")
    public ResponseEntity<List<AppointmentReponseDTO>> listAppointmentsByDate(@PathVariable LocalDate date, UUID companyId) {
        List<AppointmentReponseDTO> appointments = appointmentService.listAppointmentsByDate(date, companyId);

        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }
}
