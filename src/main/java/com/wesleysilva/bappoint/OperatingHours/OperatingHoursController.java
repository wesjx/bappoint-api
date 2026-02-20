package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/settings/operating_hours")
@Tag(name = "dev/OperatingHours", description = "Manage company operating hours.")
public class OperatingHoursController {
    private final OperatingHoursService operatingHoursService;

    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    @PostMapping("/create")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<CreateOperatingHoursDTO> createOperatingHours(@PathVariable UUID companyId, @RequestBody CreateOperatingHoursDTO operatingHoursDTO) {

        CreateOperatingHoursDTO newOperatingHours = operatingHoursService.createOperatingHours(companyId, operatingHoursDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOperatingHours);
    }

    @GetMapping("/list")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<List<OperatingHoursResponseDTO>> listOperatingHours(){
        List<OperatingHoursResponseDTO> operatingHours = operatingHoursService.getAllOperatingHours();
        return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
    }

    @GetMapping("/{operatingHoursId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<OperatingHoursAllDetailsDTO> getOperatingHoursById(@PathVariable UUID operatingHoursId) {
        OperatingHoursAllDetailsDTO operatingHours = operatingHoursService.getOperatingHoursById(operatingHoursId);
            return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
    }

    @DeleteMapping
    @Operation(
            summary = "",
            description = ""
    )
    void deleteOperatingHoursById(@PathVariable UUID operatingHoursId) {
        operatingHoursService.deleteOperatingHoursById(operatingHoursId);
    }

    @PutMapping("/update/{operatingHoursId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<UpdateOperatingHoursDTO> updateOperatingHours(
            @PathVariable UUID operatingHoursId,
            @RequestBody UpdateOperatingHoursDTO operatingHoursDTO
    ){
        UpdateOperatingHoursDTO updateOperatingHours = operatingHoursService.updateService(operatingHoursId, operatingHoursDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updateOperatingHours);
    }
}