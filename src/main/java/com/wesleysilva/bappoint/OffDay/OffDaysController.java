package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
import com.wesleysilva.bappoint.docs.OffDaysControllerDoc;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/settings/off_days")
public class OffDaysController implements OffDaysControllerDoc {
    private final OffDaysService offDaysService;

    public OffDaysController(OffDaysService offDaysService) {
        this.offDaysService = offDaysService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<CreateOffDayDTO> createOffDays(@Valid @PathVariable UUID companyId, @RequestBody CreateOffDayDTO offDaysDTO) {
        CreateOffDayDTO newOffDays = offDaysService.createOffDays(companyId, offDaysDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOffDays);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<List<OffDaysResponseDTO>> getAllOffDays(@PathVariable UUID companyId) {
        List<OffDaysResponseDTO> offDaysDTOs = offDaysService.getAllOffDays(companyId);
        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTOs);
    }

    @GetMapping("/list/{offDaysId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOffDayOwner(#offDaysId)")
    public ResponseEntity<OffDaysAllDetailsDTO> getOffDaysById(@PathVariable UUID offDaysId){
        OffDaysAllDetailsDTO offDaysDTO = offDaysService.getOffDaysById(offDaysId);

        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTO);
    }

    @DeleteMapping("/delete/{offDaysId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOffDayOwner(#offDaysId)")
    public ResponseEntity<Void> deleteOffDays(@PathVariable UUID offDaysId){
        offDaysService.deleteOffDaysById(offDaysId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{offDaysId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOffDayOwner(#offDaysId)")
    public ResponseEntity<OffDayUpdateDTO> updateOffDays(
            @PathVariable UUID offDaysId,
            @Valid @RequestBody OffDayUpdateDTO offDaysDTO
    ) {
        OffDayUpdateDTO updateOffDays = offDaysService.updateService(offDaysId, offDaysDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updateOffDays);
    }



}



