package com.wesleysilva.bappoint.settings;

import com.wesleysilva.bappoint.company.CompanyModel;
import com.wesleysilva.bappoint.company.CompanyRepository;
import com.wesleysilva.bappoint.settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.settings.dto.UpdateSettingsDTO;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class SettingsService {

    private final CompanyRepository companyRepository;
    private final SettingsMapper settingsMapper;

    public SettingsService(CompanyRepository companyRepository, SettingsMapper settingsMapper) {
        this.companyRepository = companyRepository;
        this.settingsMapper = settingsMapper;
    }

    public SettingsAllDetailsDTO getByCompanyId(UUID companyId) {
        CompanyModel company = companyRepository
                .findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        SettingsModel settings = company.getSettings();

        if (settings == null) {
            throw new SettingsNotFoundException();
        }

        return settingsMapper.toResponseAllDetails(settings);
    }

    public UpdateSettingsDTO updateByCompanyId(UUID companyId, UpdateSettingsDTO updateSettingsDTO) {
        CompanyModel company = companyRepository
                .findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        SettingsModel settings = company.getSettings();

        if (settings == null) {
            throw new SettingsNotFoundException();
        }

        settings.setAppointmentInterval(updateSettingsDTO.getAppointmentInterval());
        settings.setMaxCancellationInterval(updateSettingsDTO.getMaxCancellationInterval());

        return settingsMapper.toUpdateSettings(settings);
    }
}
