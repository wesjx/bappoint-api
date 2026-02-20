package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
import com.wesleysilva.bappoint.exceptions.OffDayNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/settings/off_days")
@Tag(name = "dev/OffDays", description = "Manage company off days.")
public class OffDaysController {
    private final OffDaysService offDaysService;

    public OffDaysController(OffDaysService offDaysService) {
        this.offDaysService = offDaysService;
    }

    @PostMapping("/create")
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
    public ResponseEntity<CreateOffDayDTO> createOffDays(@Valid @PathVariable UUID companyId, @RequestBody CreateOffDayDTO offDaysDTO) {
        CreateOffDayDTO newOffDays = offDaysService.createOffDays(companyId, offDaysDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOffDays);
    }

    @GetMapping("/list")
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
    public ResponseEntity<List<OffDaysResponseDTO>> getAllOffDays(){
        List<OffDaysResponseDTO> offDaysDTOs = offDaysService.getAllOffDays();
        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTOs);
    }

    @GetMapping("/list/{offDaysId}")
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
    public ResponseEntity<OffDaysAllDetailsDTO> getOffDaysById(@PathVariable UUID offDaysId){
        OffDaysAllDetailsDTO offDaysDTO = offDaysService.getOffDaysById(offDaysId);

        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTO);
    }

    @DeleteMapping("/delete/{offDaysId}")
    @Operation(
            summary = "Delete a off day",
            description = "Permanently removes a off day from the system by UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Off day successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Off day not found")
            }
    )
    void deleteOffDays(@PathVariable UUID offDaysId){

        if (offDaysService.getOffDaysById(offDaysId) != null ){
        offDaysService.deleteOffDaysById(offDaysId);
        } else {
            throw new OffDayNotFoundException();
        }
    }

    @PutMapping("/update/{offDaysId}")
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
    public ResponseEntity<OffDayUpdateDTO> updateOffDays(
            @PathVariable UUID offDaysId,
            @Valid @RequestBody OffDayUpdateDTO offDaysDTO
    ) {
        OffDayUpdateDTO updateOffDays = offDaysService.updateService(offDaysId, offDaysDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updateOffDays);
    }



}



