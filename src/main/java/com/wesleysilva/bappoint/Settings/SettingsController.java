package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.Settings.dto.UpdateSettingsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/settings")
@Tag(name = "dev/settings", description = "Manage company settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/list")
    @Operation(summary = "Get company settings")
    public ResponseEntity<SettingsAllDetailsDTO> getSettings(
            @PathVariable UUID companyId
    ) {
        return ResponseEntity.ok(
                settingsService.getByCompanyId(companyId)
        );
    }

    @PutMapping("/update")
    @Operation(summary = "Update company settings")
    public ResponseEntity<UpdateSettingsDTO> updateSettings(
            @PathVariable UUID companyId,
            @RequestBody UpdateSettingsDTO settingsDTO
    ) {
        return ResponseEntity.ok(
                settingsService.updateByCompanyId(companyId, settingsDTO)
        );
    }
}
