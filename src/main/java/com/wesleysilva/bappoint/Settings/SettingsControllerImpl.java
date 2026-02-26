package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Settings.doc.SettingsController;
import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.Settings.dto.UpdateSettingsDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/settings")
@Tag(name = "dev/settings", description = "Manage company settings")
public class SettingsControllerImpl implements SettingsController {

    private final SettingsService settingsService;

    public SettingsControllerImpl(SettingsService settingsService) {
        this.settingsService = settingsService;
    }


    @GetMapping("/list")
    public ResponseEntity<SettingsAllDetailsDTO> getSettings(@PathVariable UUID companyId) {
        return ResponseEntity.ok(settingsService.getByCompanyId(companyId));
    }


    @PutMapping("/update")
    public ResponseEntity<UpdateSettingsDTO> updateSettings(@PathVariable UUID companyId, @RequestBody UpdateSettingsDTO settingsDTO) {
        return ResponseEntity.ok(
                settingsService.updateByCompanyId(companyId, settingsDTO)
        );
    }
}
