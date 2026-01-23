package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.Services.ServiceMapper;
import com.wesleysilva.bappoint.Services.ServiceModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SettingsMapper {
    private final ServiceMapper serviceMapper;

    public SettingsMapper(ServiceMapper serviceMapper) {
        this.serviceMapper = serviceMapper;
    }

    public SettingsModel map(SettingsDTO settingsDTO) {
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.setId(settingsDTO.getId());
        settingsModel.setAppointment_interval(settingsDTO.getAppointment_interval());
        settingsModel.setMax_cancellation_interval(settingsDTO.getMax_cancellation_interval());

        if (settingsDTO.getServices() != null) {
            List<ServiceModel> serviceModels = settingsDTO.getServices()
                    .stream()
                    .map(serviceMapper::toEntityWithoutSettings)  // ← SEM circular reference
                    .peek(service -> service.setSettings(settingsModel))
                    .collect(Collectors.toList());
            settingsModel.setServices(serviceModels);
        }

        return settingsModel;
    }

    public SettingsDTO map(SettingsModel settingsModel) {
        SettingsDTO settingsDTO = new SettingsDTO();
        settingsDTO.setId(settingsModel.getId());
        settingsDTO.setAppointment_interval(settingsModel.getAppointment_interval());
        settingsDTO.setMax_cancellation_interval(settingsModel.getMax_cancellation_interval());

        if (settingsModel.getServices() != null) {
            settingsDTO.setServices(
                    settingsModel.getServices().stream()
                            .map(serviceMapper::map)
                            .collect(Collectors.toList())
            );
        }

        return settingsDTO;
    }
}
