package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
import com.wesleysilva.bappoint.docs.OperatingHoursControllerDoc;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/settings/operating_hours")
public class OperatingHoursController implements OperatingHoursControllerDoc {
    private final OperatingHoursService operatingHoursService;

    public OperatingHoursController(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<CreateOperatingHoursDTO> createOperatingHours(@PathVariable UUID companyId, @RequestBody CreateOperatingHoursDTO operatingHoursDTO) {

        CreateOperatingHoursDTO newOperatingHours = operatingHoursService.createOperatingHours(companyId, operatingHoursDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOperatingHours);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<List<OperatingHoursResponseDTO>> listOperatingHours(@PathVariable UUID companyId) {
        List<OperatingHoursResponseDTO> operatingHours = operatingHoursService.getAllOperatingHours(companyId);
        return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
    }

    @GetMapping("/{operatingHoursId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOperatingHoursOwner(#operatingHoursId)")
    public ResponseEntity<OperatingHoursAllDetailsDTO> getOperatingHoursById(@PathVariable UUID operatingHoursId) {
        OperatingHoursAllDetailsDTO operatingHours = operatingHoursService.getOperatingHoursById(operatingHoursId);
            return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
    }

    @DeleteMapping("/{operatingHoursId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOperatingHoursOwner(#operatingHoursId)")
    public ResponseEntity<Void> deleteOperatingHoursById(@PathVariable UUID operatingHoursId) {
        operatingHoursService.deleteOperatingHoursById(operatingHoursId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{operatingHoursId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOperatingHoursOwner(#operatingHoursId)")
    public ResponseEntity<UpdateOperatingHoursDTO> updateOperatingHours(
            @PathVariable UUID operatingHoursId,
            @RequestBody UpdateOperatingHoursDTO operatingHoursDTO
    ){
        UpdateOperatingHoursDTO updateOperatingHours = operatingHoursService.updateService(operatingHoursId, operatingHoursDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updateOperatingHours);
    }
}