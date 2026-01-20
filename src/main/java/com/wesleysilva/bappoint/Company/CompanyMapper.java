package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Settings.SettingsDTO;
import com.wesleysilva.bappoint.Settings.SettingsMapper;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyModel map(CompanyDTO companyDTO) {
        CompanyModel companyModel = new CompanyModel();

        companyModel.setId(companyDTO.getId());
        companyModel.setName(companyDTO.getName());
        companyModel.setEmail(companyDTO.getEmail());
        companyModel.setPhone(companyDTO.getPhone());
        companyModel.setAddress(companyDTO.getAddress());

        if (companyDTO.getSettings() != null) {
            companyModel.setSettings(
                    new SettingsModel(
                            companyDTO.getSettings().getId(),
                            companyDTO.getSettings().getAppointment_interval(),
                            companyDTO.getSettings().getMax_cancellation_interval()
                    )
            );
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
            companyDTO.setSettings(
                    new SettingsDTO(
                            companyModel.getSettings().getId(),
                            companyModel.getSettings().getAppointment_interval(),
                            companyModel.getSettings().getMax_cancellation_interval()
                    )
            );
        }

        return companyDTO;
    }
}
