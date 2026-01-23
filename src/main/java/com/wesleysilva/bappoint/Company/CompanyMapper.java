package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Settings.SettingsMapper;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {
    private final SettingsMapper settingsMapper;

    public CompanyMapper(SettingsMapper settingsMapper) {
        this.settingsMapper = settingsMapper;
    }

    public CompanyModel map(CompanyDTO companyDTO) {
        CompanyModel companyModel = new CompanyModel();
        companyModel.setId(companyDTO.getId());
        companyModel.setName(companyDTO.getName());
        companyModel.setEmail(companyDTO.getEmail());
        companyModel.setPhone(companyDTO.getPhone());
        companyModel.setAddress(companyDTO.getAddress());

        if (companyDTO.getSettings() != null) {
            SettingsModel settingsModel = settingsMapper.map(companyDTO.getSettings());
            companyModel.setSettings(settingsModel);
        }

        return companyModel;
    }

    public CompanyDTO map(CompanyModel companyModel) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(companyModel.getId());
        companyDTO.setName(companyModel.getName());
        companyDTO.setEmail(companyModel.getEmail());
        companyDTO.setPhone(companyModel.getPhone());
        companyDTO.setAddress(companyModel.getAddress());

        if (companyModel.getSettings() != null) {
            companyDTO.setSettings(settingsMapper.map(companyModel.getSettings()));
        }

        return companyDTO;
    }
}
