package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.Settings.dto.UpdateSettingsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private SettingsController settingsController;

    private UUID companyId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // GET /list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return settings for the company with status 200")
    void getSettings_shouldReturnSettingsWithStatus200() {
        SettingsAllDetailsDTO dto = new SettingsAllDetailsDTO();

        when(settingsService.getByCompanyId(companyId)).thenReturn(dto);

        ResponseEntity<SettingsAllDetailsDTO> response =
                settingsController.getSettings(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(settingsService).getByCompanyId(companyId);
    }

    @Test
    @DisplayName("Should call getByCompanyId exactly once with correct company ID")
    void getSettings_shouldInvokeServiceOnce() {
        when(settingsService.getByCompanyId(companyId))
                .thenReturn(new SettingsAllDetailsDTO());

        settingsController.getSettings(companyId);

        verify(settingsService, times(1)).getByCompanyId(companyId);
        verifyNoMoreInteractions(settingsService);
    }

    // -------------------------------------------------------------------------
    // PUT /update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update settings and return 200 with updated body")
    void updateSettings_shouldReturn200WithBody() {
        UpdateSettingsDTO requestDTO = new UpdateSettingsDTO();
        UpdateSettingsDTO responseDTO = new UpdateSettingsDTO();

        when(settingsService.updateByCompanyId(eq(companyId), any(UpdateSettingsDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<UpdateSettingsDTO> response =
                settingsController.updateSettings(companyId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(settingsService).updateByCompanyId(companyId, requestDTO);
    }

    @Test
    @DisplayName("Should call updateByCompanyId exactly once with correct arguments")
    void updateSettings_shouldInvokeServiceOnce() {
        UpdateSettingsDTO requestDTO = new UpdateSettingsDTO();

        when(settingsService.updateByCompanyId(any(), any(UpdateSettingsDTO.class)))
                .thenReturn(new UpdateSettingsDTO());

        settingsController.updateSettings(companyId, requestDTO);

        verify(settingsService, times(1)).updateByCompanyId(companyId, requestDTO);
        verifyNoMoreInteractions(settingsService);
    }
}
