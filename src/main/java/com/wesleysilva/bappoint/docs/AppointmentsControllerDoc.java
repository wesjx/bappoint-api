package com.wesleysilva.bappoint.docs;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "dev/Appointments", description = "Endpoint for manage appointment scheduling")
public interface AppointmentsControllerDoc {

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
    ResponseEntity<List<SlotTimesDTO>> getAvailableTimes(
            @PathVariable UUID companyId,
            @RequestParam String date);

    @Operation(summary = "Create appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "Time slot conflict"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<CreateAppointmentDTO> createAppointment(
            @PathVariable UUID companyId,
            @RequestBody CreateAppointmentDTO appointmentDTO);

    @Operation(summary = "List appointments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<List<AppointmentAllDetailsDTO>> listAppointments(
            @RequestParam int page,
            @RequestParam int itemsPerPage,
            @PathVariable UUID companyId);

    @Operation(summary = "List appointments by date")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<List<AppointmentReponseDTO>> listAppointmentsByDate(
            @RequestParam LocalDate date,
            @PathVariable UUID companyId);

    @Operation(summary = "Get appointment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Appointment not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<AppointmentAllDetailsDTO> listAppointmentById(
            @PathVariable UUID appointmentId);

    @Operation(summary = "Update appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "ID not found"),
            @ApiResponse(responseCode = "409", description = "Time slot conflict"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<UpdateAppointmentDTO> updateAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody UpdateAppointmentDTO appointmentDTO);


    @Operation(summary = "Delete appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "ID not found")
    })
    ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId);
}

