package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.Settings.dto.UpdateSettingsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<SettingsAllDetailsDTO> getSettings(
            @PathVariable UUID companyId
    ) {
        return ResponseEntity.ok(
                settingsService.getByCompanyId(companyId)
        );
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<UpdateSettingsDTO> updateSettings(
            @PathVariable UUID companyId,
            @RequestBody UpdateSettingsDTO settingsDTO
    ) {
        return ResponseEntity.ok(
                settingsService.updateByCompanyId(companyId, settingsDTO)
        );
    }
}
