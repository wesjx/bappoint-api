package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("company/{companyId}/settings/off_days")
@Tag(name = "dev/Company", description = "Manage company off days.")
public class OffDaysController {
    private final OffDaysService offDaysService;
    private final CompanyRepository companyRepository;

    public OffDaysController(OffDaysService offDaysService, CompanyRepository companyRepository) {
        this.offDaysService = offDaysService;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/create")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<OffDaysDTO> createOffDays(@PathVariable UUID companyId, @RequestBody OffDaysDTO offDaysDTO) {
        SettingsModel settings = companyRepository.findById(companyId)
                .map(CompanyModel::getSettings)
                .orElseThrow(() -> new RuntimeException("Company or settings not found"));

        OffDaysDTO newOffDays = offDaysService.createOffDays(settings.getId(), offDaysDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOffDays);
    }

    @GetMapping("/list")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<String> getAllOffDays(){
        List<OffDaysDTO> offDaysDTOs = offDaysService.getAllOffDays();
        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTOs.toString());
    }

    @GetMapping("/list/{offDaysId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<OffDaysDTO> getOffDaysById(@PathVariable UUID offDaysId){
        OffDaysDTO offDaysDTO = offDaysService.getOffDaysById(offDaysId);

        if(offDaysDTO != null){
            return ResponseEntity.status(HttpStatus.OK).body(offDaysDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{offDaysId}")
    @Operation(
            summary = "",
            description = ""
    )
    void deleteOffDays(@PathVariable UUID offDaysId){
        offDaysService.deleteOffDaysById(offDaysId);
    }

    @PutMapping("/update/{offDaysId}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<OffDaysDTO> updateOffDays(
            @PathVariable UUID offDaysId,
            @RequestBody OffDaysDTO offDaysDTO
    ){
        OffDaysDTO updateOffDays = offDaysService.updateService(offDaysId, offDaysDTO);
        if (updateOffDays != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updateOffDays);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}



