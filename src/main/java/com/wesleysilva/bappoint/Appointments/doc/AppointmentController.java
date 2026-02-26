package com.wesleysilva.bappoint.Appointments.doc;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface AppointmentController {


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
    ResponseEntity<List<SlotTimesDTO>> getAvailableTimes(@PathVariable UUID companyId, @RequestParam String date);



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
    ResponseEntity<List<AppointmentReponseDTO>> listAppointments(@RequestParam int page, int itemsPerPage);



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
    ResponseEntity<AppointmentAllDetailsDTO> listAppointmentById(@PathVariable UUID appointmentId);



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
    ResponseEntity<CreateAppointmentDTO> createAppointment(@PathVariable UUID companyId,
                                                                  @RequestBody CreateAppointmentDTO appointmentDTO);



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
    ResponseEntity<?> updateAppointment(@RequestBody UpdateAppointmentDTO appointmentResponseDTO,
                                        @PathVariable UUID appointmentId);



    @Operation(summary = "Update appointment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delete successfully"),
            @ApiResponse(responseCode = "404", description = "ID not found"),
    })
    ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId);
}
