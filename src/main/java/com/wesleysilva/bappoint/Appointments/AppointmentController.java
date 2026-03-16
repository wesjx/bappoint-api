package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import com.wesleysilva.bappoint.clerk.ClerkAuthContext;
import com.wesleysilva.bappoint.clerk.ClerkSecurityService;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "dev/Appointments", description = "Manage appointment scheduling")
public class AppointmentController {

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
    @Operation(summary = "List available times")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "available-time-list", summary = "List of available times",
                                    value = """
                                            [{ "start": "2026-02-06T13:00", "end": "2026-02-06T13:15" }]
                                            """))),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<List<SlotTimesDTO>> getAvailableTimes(
            @PathVariable UUID companyId,
            @RequestParam String date) {
        return ResponseEntity.ok(slotsTimesService.findAvailableSlots(companyId, date));
    }


    @PostMapping("/create")
    @Operation(summary = "Create appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Time slot conflict"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<CreateAppointmentDTO> createAppointment(
            @PathVariable UUID companyId,
            @RequestBody CreateAppointmentDTO appointmentDTO) {
        CreateAppointmentDTO appointment = appointmentService.createAppointment(appointmentDTO, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/list")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('MASTER', 'COMPANY_ADMIN')")
    @Operation(summary = "List appointments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
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
    @Operation(summary = "List appointments by date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<List<AppointmentReponseDTO>> listAppointmentsByDate(
            @RequestParam LocalDate date,
            @PathVariable UUID companyId) {
        List<AppointmentReponseDTO> appointments = appointmentService.listAppointmentsByDate(date, companyId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isAppointmentOwner(#appointmentId)")
    @Operation(summary = "Get appointment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<AppointmentAllDetailsDTO> listAppointmentById(
            @PathVariable UUID appointmentId) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(appointmentId));
    }

    @PutMapping("/update/{appointmentId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isAppointmentOwner(#appointmentId)")
    @Operation(summary = "Update appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "ID not found"),
            @ApiResponse(responseCode = "409", description = "Time slot conflict"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<UpdateAppointmentDTO> updateAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody UpdateAppointmentDTO appointmentDTO) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, appointmentDTO));
    }

    @DeleteMapping("/delete/{appointmentId}")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "Delete appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "ID not found")
    })
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }
}