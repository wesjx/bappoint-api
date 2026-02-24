package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Appointments.AppointmentMapper;
import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
import com.wesleysilva.bappoint.Settings.SettingsMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CompanyMapper {
    private final SettingsMapper settingsMapper;
    private final AppointmentMapper appointmentMapper;

    public CompanyMapper(SettingsMapper settingsMapper, AppointmentMapper appointmentMapper) {
        this.settingsMapper = settingsMapper;
        this.appointmentMapper = appointmentMapper;
    }

    public CreateCompanyDTO toCreate(CompanyModel companyModel) {
        CreateCompanyDTO companyDto = new CreateCompanyDTO();

        companyDto.setName(companyModel.getName());
        companyDto.setEmail(companyModel.getEmail());
        companyDto.setPhone(companyModel.getPhone());
        companyDto.setAddress(companyModel.getAddress());
        companyDto.setStripeAccountId(companyModel.getStripeAccountId());

        if (companyModel.getSettings() != null) {
            companyDto.setSettings(settingsMapper.toCreate(companyModel.getSettings()));
        }

        return companyDto;
    }

    public CompanyResponseDTO toResponseDTO(CompanyModel companyModel) {
        CompanyResponseDTO companyDTO = new CompanyResponseDTO();
        companyDTO.setId(companyModel.getId());
        companyDTO.setName(companyModel.getName());
        companyDTO.setEmail(companyModel.getEmail());
        companyDTO.setPhone(companyModel.getPhone());
        companyDTO.setAddress(companyModel.getAddress());

        if (companyModel.getSettings() != null) {
            companyDTO.setSettings(settingsMapper.toResponse(companyModel.getSettings()));
        }

        return companyDTO;
    }

    public CompanyDetailsResponseDTO toDetailsResponseDTO(CompanyModel companyModel) {
        CompanyDetailsResponseDTO companyDTO = new CompanyDetailsResponseDTO();
        companyDTO.setId(companyModel.getId());
        companyDTO.setName(companyModel.getName());
        companyDTO.setEmail(companyModel.getEmail());
        companyDTO.setPhone(companyModel.getPhone());
        companyDTO.setAddress(companyModel.getAddress());
        companyDTO.setStripeAccountId(companyModel.getStripeAccountId());

        if (companyModel.getSettings() != null) {
            companyDTO.setSettings(settingsMapper.toResponseAllDetails(companyModel.getSettings()));
        }

        if (companyModel.getAppointments() != null) {
            companyDTO.setAppointments(
                    companyModel.getAppointments().stream()
                            .map(appointmentMapper::toResponseDTO)
                            .collect(Collectors.toList())
            );
        }

        return companyDTO;
    }

    public CompanyModel toUpdateCompany(UpdateCompanyDTO updateCompanyDTO, CompanyModel existingCompany) {
        if (updateCompanyDTO.getName() != null) existingCompany.setName(updateCompanyDTO.getName());
        if (updateCompanyDTO.getEmail() != null) existingCompany.setEmail(updateCompanyDTO.getEmail());
        if (updateCompanyDTO.getPhone() != null) existingCompany.setPhone(updateCompanyDTO.getPhone());
        if (updateCompanyDTO.getAddress() != null) existingCompany.setAddress(updateCompanyDTO.getAddress());
        if (updateCompanyDTO.getStripeAccountId() != null) existingCompany.setStripeAccountId(updateCompanyDTO.getStripeAccountId());

        if (updateCompanyDTO.getSettings() != null) {
            settingsMapper.toUpdateFromDTO(
                    updateCompanyDTO.getSettings(),
                    existingCompany.getSettings()
            );
        }

        return existingCompany;
    }
}
