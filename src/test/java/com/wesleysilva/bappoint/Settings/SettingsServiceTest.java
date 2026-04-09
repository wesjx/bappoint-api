package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.Settings.dto.UpdateSettingsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private SettingsMapper settingsMapper;

    @InjectMocks
    private SettingsService settingsService;

    private UUID companyId;
    private CompanyModel company;
    private SettingsModel settings;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();

        settings = new SettingsModel();
        settings.setId(UUID.randomUUID());
        settings.setAppointmentInterval(AppointmentInterval.MINUTES_30);
        settings.setMaxCancellationInterval(1);

        company = new CompanyModel();
        company.setId(companyId);
        company.setSettings(settings);
    }

    // -------------------------------------------------------------------------
    // getByCompanyId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return settings details when company and settings exist")
    void getByCompanyId_shouldReturnSettings() {
        SettingsAllDetailsDTO dto = new SettingsAllDetailsDTO();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(settingsMapper.toResponseAllDetails(settings)).thenReturn(dto);

        SettingsAllDetailsDTO result = settingsService.getByCompanyId(companyId);

        assertNotNull(result);
        assertEquals(dto, result);
        verify(companyRepository).findById(companyId);
        verify(settingsMapper).toResponseAllDetails(settings);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist")
    void getByCompanyId_shouldThrowWhenCompanyNotFound() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                settingsService.getByCompanyId(companyId)
        );

        verifyNoInteractions(settingsMapper);
    }

    @Test
    @DisplayName("Should throw SettingsNotFoundException when company has no settings")
    void getByCompanyId_shouldThrowWhenSettingsIsNull() {
        company.setSettings(null);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(SettingsNotFoundException.class, () ->
                settingsService.getByCompanyId(companyId)
        );

        verifyNoInteractions(settingsMapper);
    }

    // -------------------------------------------------------------------------
    // updateByCompanyId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update settings fields and return updated DTO")
    void updateByCompanyId_shouldUpdateSuccessfully() {
        UpdateSettingsDTO updateDTO = new UpdateSettingsDTO();
        updateDTO.setAppointmentInterval(AppointmentInterval.MINUTES_60);
        updateDTO.setMaxCancellationInterval(2);

        UpdateSettingsDTO responseDTO = new UpdateSettingsDTO();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(settingsMapper.toUpdateSettings(settings)).thenReturn(responseDTO);

        UpdateSettingsDTO result = settingsService.updateByCompanyId(companyId, updateDTO);

        assertNotNull(result);

        // Verify fields were mutated on the model before mapping
        assertEquals(AppointmentInterval.MINUTES_60, settings.getAppointmentInterval());
        assertEquals(2, settings.getMaxCancellationInterval());

        verify(settingsMapper).toUpdateSettings(settings);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist on update")
    void updateByCompanyId_shouldThrowWhenCompanyNotFound() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                settingsService.updateByCompanyId(companyId, new UpdateSettingsDTO())
        );

        verifyNoInteractions(settingsMapper);
    }

    @Test
    @DisplayName("Should throw SettingsNotFoundException when company has no settings on update")
    void updateByCompanyId_shouldThrowWhenSettingsIsNull() {
        company.setSettings(null);

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(SettingsNotFoundException.class, () ->
                settingsService.updateByCompanyId(companyId, new UpdateSettingsDTO())
        );

        verifyNoInteractions(settingsMapper);
    }
}
