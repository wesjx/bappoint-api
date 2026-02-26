package com.wesleysilva.bappoint.OffDay.doc;

import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

@Tag(name = "dev/OffDays", description = "Manage company off days.")
public interface OffDaysController {

    @Operation(summary = "Create off day")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "offDay",
                                            summary = "Off day created",
                                            value = """
                                                {
                                                    "id": "899bfab4-4553-4af1-a884-6cfad98eef16",
                                                    "reason": "holiday",
                                                    "date": "2026-02-22",
                                                    "offDaysType": "HOLIDAY"
                                                }
                                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Company or Settings not found")
    })
    ResponseEntity<CreateOffDayDTO> createOffDays(@Valid @PathVariable UUID companyId,
                                                  @RequestBody CreateOffDayDTO offDaysDTO);



    @Operation(summary = "List all off days")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "offDays",
                                            summary = "List of off days",
                                            value = """
                                             [
                                                {
                                                    "id": "899bfab4-4553-4af1-a884-6cfad98eef16",
                                                    "reason": "holiday",
                                                    "date": "2026-02-22",
                                                    "offDaysType": "HOLIDAY"
                                                }
                                             ]
                                             """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<List<OffDaysResponseDTO>> getAllOffDays();



    @Operation(summary = "List off day by id")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "offDay",
                                            summary = "Off day by id",
                                            value = """
                                                {
                                                    "id": "899bfab4-4553-4af1-a884-6cfad98eef16",
                                                    "reason": "holiday",
                                                    "date": "2026-02-22",
                                                    "offDaysType": "HOLIDAY"
                                                }
                                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<OffDaysAllDetailsDTO> getOffDaysById(@PathVariable UUID offDaysId);




    @Operation(
            summary = "Delete a off day",
            description = "Permanently removes a off day from the system by UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Off day successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Off day not found")
            }
    )
    void deleteOffDays(@PathVariable UUID offDaysId);




    @Operation(summary = "Update off day")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Off day updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "off day updated",
                                            summary = "Off day updated example",
                                            value = """
                                                {
                                                    "id": "a1b2c3d4-e5f6-7890-ab12-cd34ef56gh78",
                                                    "reason": "Company team meeting",
                                                    "date": "2026-03-10",
                                                    "offDaysType": "EVENT"
                                                }
                                                """
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Off day ID not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    ResponseEntity<OffDayUpdateDTO> updateOffDays(@PathVariable UUID offDaysId,
                                                  @Valid @RequestBody OffDayUpdateDTO offDaysDTO);
}
