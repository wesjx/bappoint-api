package com.wesleysilva.bappoint.OperatingHours.doc;

import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

@Tag(name = "dev/OperatingHours", description = "Manage company operating hours.")
public interface OperatingHoursController {


    @Operation(summary = "Create operating hours",
            description = "Creates operating hours for a company's schedule. Validates weekday, times, and active status.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Operating hours created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "operatingHours",
                                            summary = "Operating hours created",
                                            value = """
                        {
                            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                            "weekday": "MONDAY",
                            "isActive": true,
                            "startTime": "09:00:00",
                            "endTime": "18:00:00",
                            "lunchStartTime": "12:30:00",
                            "lunchEndTime": "13:30:00"
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data - missing required fields, invalid times, or weekday"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Operating hours already exists for this weekday and company"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<CreateOperatingHoursDTO> createOperatingHours(@PathVariable UUID companyId,
                                                                 @RequestBody CreateOperatingHoursDTO operatingHoursDTO);



    @Operation(
            summary = "List all operating hours",
            description = "Returns all operating hours records from the system. Useful for admin dashboards or schedule overview."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Operating hours listed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "operatingHoursList",
                                            summary = "List of operating hours",
                                            value = """
                        [
                            {
                                "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                "weekday": "MONDAY",
                                "isActive": true,
                                "startTime": "09:00:00",
                                "endTime": "18:00:00",
                                "lunchStartTime": "12:30:00",
                                "lunchEndTime": "13:30:00",
                                "company": {
                                    "id": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                    "name": "Barber XPTO"
                                }
                            },
                            {
                                "id": "b2c3d4e5-f6g7-8901-bcde-f23456789012",
                                "weekday": "TUESDAY",
                                "isActive": false,
                                "startTime": "10:00:00",
                                "endTime": "17:00:00"
                            }
                        ]
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<List<OperatingHoursResponseDTO>> listOperatingHours();



    @Operation(
            summary = "Get operating hours by ID",
            description = "Retrieves complete details of a specific operating hours record by its UUID. Returns 404 if not found."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Operating hours found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "operatingHoursDetails",
                                            summary = "Complete operating hours details",
                                            value = """
                        {
                            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                            "weekday": "MONDAY",
                            "isActive": true,
                            "startTime": "09:00:00",
                            "endTime": "18:00:00",
                            "lunchStartTime": "12:30:00",
                            "lunchEndTime": "13:30:00",
                            "company": {
                                "id": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                "name": "Barber XPTO",
                                "email": "xpto@barba.com",
                                "phone": "8888888",
                                "address": "Rua das Flores, 123"
                            },
                            "settings": {
                                "id": "578466da-a125-4150-a235-a94d16a81736",
                                "appointmentInterval": "THIRTY_MINUTES"
                            }
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Operating hours not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<OperatingHoursAllDetailsDTO> getOperatingHoursById(@PathVariable UUID operatingHoursId);



    @Operation(
            summary = "Delete operating hours by ID",
            description = "Deletes a specific operating hours record by its UUID. Returns 204 No Content on success."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Operating hours deleted successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Operating hours not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    void deleteOperatingHoursById(@PathVariable UUID operatingHoursId);


    @Operation(
            summary = "Update operating hours",
            description = "Updates an existing operating hours record by UUID. Required fields: startTime, endTime, weekday. Optional: lunch times and active status."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Operating hours updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "updatedOperatingHours",
                                            summary = "Operating hours updated",
                                            value = """
                        {
                            "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                            "weekday": "MONDAY",
                            "isActive": true,
                            "startTime": "08:30:00",
                            "endTime": "19:00:00",
                            "lunchStartTime": "12:00:00",
                            "lunchEndTime": "13:00:00"
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data - missing required fields or invalid times"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Operating hours or company not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<UpdateOperatingHoursDTO> updateOperatingHours(@PathVariable UUID operatingHoursId,
                                                                 @RequestBody UpdateOperatingHoursDTO operatingHoursDTO);

}
