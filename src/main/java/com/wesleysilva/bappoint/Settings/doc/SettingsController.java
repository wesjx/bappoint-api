package com.wesleysilva.bappoint.Settings.doc;

import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.Settings.dto.UpdateSettingsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.UUID;

public interface SettingsController {


    @Operation(
            summary = "Get company settings",
            description = "Retrieves complete company settings with all nested details (services, operating hours, off days). Returns 404 if company or settings not found."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Company settings retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "companySettings",
                                            summary = "Complete settings with all nested entities",
                                            value = """
                        {
                            "id": "578466da-a125-4150-a235-a94d16a81736",
                            "appointmentInterval": "THIRTY_MINUTES",
                            "maxCancellationInterval": 24,
                            "services": [
                                {
                                    "id": "f5e590a0-1514-4bb6-b8cb-8313e1b38698",
                                    "name": "Men's Haircut",
                                    "price": 50.00,
                                    "durationMinutes": 30,
                                    "isActive": true
                                }
                            ],
                            "operatingHours": [
                                {
                                    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                    "weekday": "MONDAY",
                                    "isActive": true,
                                    "startTime": "09:00:00",
                                    "endTime": "18:00:00"
                                }
                            ],
                            "offDays": [
                                {
                                    "id": "c3d4e5f6-g7h8-9012-3456-789abcdef012",
                                    "date": "2026-02-20",
                                    "reason": "National Holiday"
                                }
                            ]
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company or settings not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<SettingsAllDetailsDTO> getSettings(@PathVariable UUID companyId);



    @Operation(
            summary = "Update company settings",
            description = "Updates company settings including appointment interval, cancellation rules, services, operating hours and off days. Partial updates supported for nested collections."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Company settings updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "updatedSettings",
                                            summary = "Updated company settings",
                                            value = """
                        {
                            "id": "578466da-a125-4150-a235-a94d16a81736",
                            "appointmentInterval": "FIFTEEN_MINUTES",
                            "maxCancellationInterval": 48,
                            "services": [
                                {
                                    "id": "f5e590a0-1514-4bb6-b8cb-8313e1b38698",
                                    "name": "Men's Haircut",
                                    "price": 50.00,
                                    "durationMinutes": 30
                                }
                            ],
                            "operatingHours": [
                                {
                                    "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                    "weekday": "MONDAY",
                                    "startTime": "08:00:00",
                                    "endTime": "20:00:00"
                                }
                            ],
                            "offDays": []
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data - missing required fields or validation errors in nested objects"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company or settings not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<UpdateSettingsDTO> updateSettings(@PathVariable UUID companyId, @RequestBody UpdateSettingsDTO settingsDTO);

}
