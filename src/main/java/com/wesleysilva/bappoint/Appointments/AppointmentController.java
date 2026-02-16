package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("company/{companyId}/appointments")
@Tag(name = "dev/Appointments", description = "Manage appointment scheduling")
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
    @Operation(summary = "List available times")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "available-time-list",
                                            summary = "List of available times",
                                            value = """
                                                    [
                                                      {
                                                        "start": "2026-02-06T13:00",
                                                        "end": "2026-02-06T13:15"
                                                      }
                                                    ]
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<List<SlotTimesDTO>> getAvailableTimes(@PathVariable UUID companyId, @RequestParam String date) {
        List<SlotTimesDTO> slotTimes = slotsTimesService.findAvailableSlots(companyId, date);
        return ResponseEntity.ok(slotTimes);
    }

    @GetMapping("/list")
    @Transactional(readOnly = true)
    @Operation(summary = "List appointments")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "appointment-list",
                                            summary = "List of appointments",
                                            value = """
                                                    [
                                                                       {
                                                                          "appointmentDate": "2026-02-12",
                                                                          "appointmentStatus": "CONFIRMED",
                                                                          "companyId": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                                                          "costumerEmail": "john.silva@email.com",
                                                                          "costumerName": "John Silva",
                                                                          "costumerPhone": "+353 083 123-4567",
                                                                          "endTime": "2026-02-12T12:45:00",
                                                                          "id": "6446b6a6-5f52-407e-bc53-56c1f7a95f3e",
                                                                          "serviceIds": [
                                                                              "f5e590a0-1514-4bb6-b8cb-8313e1b38698"
                                                                          ],
                                                                          "startTime": "2026-02-12T12:00:00",
                                                                          "totalAmount": 100.0
                                                                       }
                                                    ]
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<List<AppointmentAllDetailsDTO>> listAppointments(@RequestParam int page, int itemsPerPage) {
        List<AppointmentAllDetailsDTO> appointments = appointmentService.listAppointments(page, itemsPerPage);

        return ResponseEntity.status(HttpStatus.OK).body(appointments);
    }

    @GetMapping("/{appointmentId}")
    @Operation(summary = "List appointments")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "appointment-by-id-list",
                                            summary = "List of appointments filtered by id",
                                            value = """
                                                                       {
                                                                          "appointmentDate": "2026-02-12",
                                                                          "appointmentStatus": "CONFIRMED",
                                                                          "companyId": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                                                          "costumerEmail": "john.silva@email.com",
                                                                          "costumerName": "John Silva",
                                                                          "costumerPhone": "+353 083 123-4567",
                                                                          "endTime": "2026-02-12T12:45:00",
                                                                          "id": "6446b6a6-5f52-407e-bc53-56c1f7a95f3e",
                                                                          "serviceIds": [
                                                                              "f5e590a0-1514-4bb6-b8cb-8313e1b38698"
                                                                          ],
                                                                          "startTime": "2026-02-12T12:00:00",
                                                                          "totalAmount": 100.0
                                                                       }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    public ResponseEntity<AppointmentAllDetailsDTO> listAppointmentById(@PathVariable UUID appointmentId) {
        AppointmentAllDetailsDTO appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PostMapping("/create")
    @Operation(summary = "Create appointment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "appointment",
                                            summary = "Appointment created",
                                            value = """
                                                    [
                                                                       {
                                                                          "appointmentDate": "2026-02-12",
                                                                          "appointmentStatus": "CONFIRMED",
                                                                          "companyId": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                                                          "costumerEmail": "john.silva@email.com",
                                                                          "costumerName": "John Silva",
                                                                          "costumerPhone": "+353 083 123-4567",
                                                                          "endTime": "2026-02-12T12:45:00",
                                                                          "id": "6446b6a6-5f52-407e-bc53-56c1f7a95f3e",
                                                                          "serviceIds": [
                                                                              "f5e590a0-1514-4bb6-b8cb-8313e1b38698"
                                                                          ],
                                                                          "startTime": "2026-02-12T12:00:00",
                                                                          "totalAmount": 100.0
                                                                       }
                                                    ]
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Time slot conflict")
    })
    public ResponseEntity<CreateAppointmentDTO> createAppointment(@PathVariable UUID companyId, @RequestBody CreateAppointmentDTO appointmentDTO) {

        CreateAppointmentDTO appointment = appointmentService.createAppointment(appointmentDTO, companyId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }


    @PutMapping("/update/{appointmentId}")
    @Operation(summary = "Update appointment")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "appointment updated",
                                            summary = "Appointment updated",
                                            value = """
                                                                       {
                                                                          "appointmentDate": "2026-02-12",
                                                                          "appointmentStatus": "CONFIRMED",
                                                                          "companyId": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                                                          "costumerEmail": "john.silva@email.com",
                                                                          "costumerName": "John Silva",
                                                                          "costumerPhone": "+353 083 123-4567",
                                                                          "endTime": "2026-02-12T12:45:00",
                                                                          "id": "6446b6a6-5f52-407e-bc53-56c1f7a95f3e",
                                                                          "serviceIds": [
                                                                              "f5e590a0-1514-4bb6-b8cb-8313e1b38698"
                                                                          ],
                                                                          "startTime": "2026-02-12T12:00:00",
                                                                          "totalAmount": 100.0
                                                                       }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error"),
            @ApiResponse(responseCode = "404", description = "ID not found"),
            @ApiResponse(responseCode = "409", description = "Time slot conflict")
    })
    public ResponseEntity<?> updateAppointment(@RequestBody UpdateAppointmentDTO appointmentResponseDTO, @PathVariable UUID appointmentId) {
        UpdateAppointmentDTO appointment = appointmentService.updateAppointment(appointmentId, appointmentResponseDTO);

        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{appointmentId}")
    @Operation(summary = "Update appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delete successfully"),
            @ApiResponse(responseCode = "404", description = "ID not found"),
    })
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
