package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/company/{companyId}/settings/operating_hours")
@Tag(name = "dev/OperatingHours", description = "Manage company operating hours.")
public class OperatingHoursController {
    private final OperatingHoursService operatingHoursService;
    private final CompanyRepository companyRepository;

    public OperatingHoursController(OperatingHoursService operatingHoursService, CompanyRepository companyRepository) {
        this.operatingHoursService = operatingHoursService;
        this.companyRepository = companyRepository;
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
    public ResponseEntity<String> listOperatingHours(){
        List<OperatingHoursDTO> operatingHours = operatingHoursService.getAllOperatingHours();
        return ResponseEntity.status(HttpStatus.OK).body(operatingHours.toString());
    }

    @GetMapping("/{operatingHoursId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<OperatingHoursDTO> getOperatingHoursById(@PathVariable UUID operatingHoursId) {
        OperatingHoursDTO operatingHours = operatingHoursService.getOperatingHoursById(operatingHoursId);

        if (operatingHours != null) {
            return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
        } else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
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
    public ResponseEntity<OperatingHoursDTO> updateOperatingHours(
            @PathVariable UUID operatingHoursId,
            @RequestBody OperatingHoursDTO operatingHoursDTO
    ){
        OperatingHoursDTO updateOperatingHours = operatingHoursService.updateService(operatingHoursId, operatingHoursDTO);
        if (updateOperatingHours != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updateOperatingHours);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
