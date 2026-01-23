package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Services.ServiceDTO;
import com.wesleysilva.bappoint.Services.ServiceMapper;
import com.wesleysilva.bappoint.Settings.SettingsDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CompanyMapper {

    private final ServiceMapper serviceMapper;

    public CompanyMapper(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

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

        if (companyDTO.getServices() != null) {
            List<ServiceDTO> serviceDTOs = companyDTO.getServices();
            companyModel.setServices(
                    serviceDTOs.stream()
                            .map(serviceMapper::map)
                            .collect(Collectors.toList())
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

        if (companyModel.getServices() != null) {
            List<ServiceDTO> serviceDTOs = companyModel.getServices()
                    .stream()
                    .map(serviceMapper::map)
                    .collect(Collectors.toList());
            companyDTO.setServices(serviceDTOs);
        }

        return companyDTO;
    }
}
