package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import com.wesleysilva.bappoint.clerk.ClerkAuthContext;
import com.wesleysilva.bappoint.clerk.ClerkSecurityService;
import com.wesleysilva.bappoint.docs.AppointmentsControllerDoc;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/appointments")
public class AppointmentController implements AppointmentsControllerDoc {

    @Autowired
    private final AppointmentService appointmentService;
    private final SlotsTimesService slotsTimesService;
    private final ClerkAuthContext clerkAuthContext;
    private final ClerkSecurityService clerkSecurityService;

    public AppointmentController(AppointmentService appointmentService,
                                 SlotsTimesService slotsTimesService,
                                 ClerkAuthContext clerkAuthContext,
                                 ClerkSecurityService clerkSecurityService) {
        this.appointmentService = appointmentService;
        this.slotsTimesService = slotsTimesService;
        this.clerkAuthContext = clerkAuthContext;
        this.clerkSecurityService = clerkSecurityService;
    }

    @GetMapping("/available-times")
    public ResponseEntity<List<SlotTimesDTO>> getAvailableTimes(
            @PathVariable UUID companyId,
            @RequestParam String date) {
        return ResponseEntity.ok(slotsTimesService.findAvailableSlots(companyId, date));
    }


    @PostMapping("/create")
    public ResponseEntity<CreateAppointmentDTO> createAppointment(
            @PathVariable UUID companyId,
            @RequestBody CreateAppointmentDTO appointmentDTO) {
        CreateAppointmentDTO appointment = appointmentService.createAppointment(appointmentDTO, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/list")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('MASTER', 'COMPANY_ADMIN')")
    public ResponseEntity<List<AppointmentAllDetailsDTO>> listAppointments(
            @RequestParam int page,
            @RequestParam int itemsPerPage,
            @PathVariable UUID companyId) {

        List<AppointmentAllDetailsDTO> appointments;

        if (clerkAuthContext.isMaster()) {
            appointments = appointmentService.listAppointments(companyId, page, itemsPerPage);
        } else {
            if (!clerkSecurityService.isCompanyOwner(companyId)) {
                throw new CompanyNotFoundException();
            }
            appointments = appointmentService.listAppointments(companyId, page, itemsPerPage);
        }

        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/by-date")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<List<AppointmentReponseDTO>> listAppointmentsByDate(
            @RequestParam LocalDate date,
            @PathVariable UUID companyId) {
        List<AppointmentReponseDTO> appointments = appointmentService.listAppointmentsByDate(date, companyId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isAppointmentOwner(#appointmentId)")
    public ResponseEntity<AppointmentAllDetailsDTO> listAppointmentById(
            @PathVariable UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(appointmentId));
    }

    @PutMapping("/update/{appointmentId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isAppointmentOwner(#appointmentId)")
    public ResponseEntity<UpdateAppointmentDTO> updateAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody UpdateAppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, appointmentDTO));
    }

    @DeleteMapping("/delete/{appointmentId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }
}