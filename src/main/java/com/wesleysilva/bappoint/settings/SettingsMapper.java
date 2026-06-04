package com.wesleysilva.bappoint.settings;

import com.wesleysilva.bappoint.offday.OffDaysMapper;
import com.wesleysilva.bappoint.operatinghours.OperatingHoursMapper;
import com.wesleysilva.bappoint.services.ServiceMapper;
import com.wesleysilva.bappoint.settings.dto.CreateSettingsDTO;
import com.wesleysilva.bappoint.settings.dto.SettingsAllDetailsDTO;
import com.wesleysilva.bappoint.settings.dto.SettingsResponseDTO;
import com.wesleysilva.bappoint.settings.dto.UpdateSettingsDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SettingsMapper {
    private final ServiceMapper serviceMapper;
    private final OperatingHoursMapper operatingHoursMapper;
    private final OffDaysMapper offDaysMapper;

    public SettingsMapper(ServiceMapper serviceMapper, OperatingHoursMapper operatingHoursMapper, OffDaysMapper offDaysMapper) {
        this.serviceMapper = serviceMapper;
        this.operatingHoursMapper = operatingHoursMapper;
        this.offDaysMapper = offDaysMapper;
    }

    public CreateSettingsDTO toCreate(SettingsModel settingsModel) {
        CreateSettingsDTO settingsDTO = new CreateSettingsDTO();
        settingsDTO.setAppointmentInterval(settingsModel.getAppointmentInterval());
        settingsDTO.setMaxCancellationInterval(settingsModel.getMaxCancellationInterval());

        if (settingsModel.getServices() != null) {
            settingsDTO.setServices(
                    settingsModel.getServices().stream()
                            .map(serviceMapper::toCreate)
                            .collect(Collectors.toList())
            );
        }

        if (settingsModel.getOperatingHours() != null) {
            settingsDTO.setOperatingHours(
                    settingsModel.getOperatingHours().stream()
                            .map(operatingHoursMapper::toCreate)
                            .collect(Collectors.toList())
            );
        }

        if (settingsModel.getOffDays() != null) {
            settingsDTO.setOffDays(
                    settingsModel.getOffDays().stream()
                            .map(offDaysMapper::toCreate)
                            .collect(Collectors.toList())
            );
        }

        return settingsDTO;
    }

    public SettingsResponseDTO toResponse(SettingsModel settingsModel) {
        SettingsResponseDTO settingsResponseDTO = new SettingsResponseDTO();
        settingsResponseDTO.setId(settingsModel.getId());
        settingsResponseDTO.setAppointmentInterval(settingsModel.getAppointmentInterval());
        settingsResponseDTO.setMaxCancellationInterval(settingsModel.getMaxCancellationInterval());

        return settingsResponseDTO;
    }

    public SettingsAllDetailsDTO toResponseAllDetails(SettingsModel settingsModel) {
        SettingsAllDetailsDTO settingsDTO = new SettingsAllDetailsDTO();

        settingsDTO.setId(settingsModel.getId());
        settingsDTO.setAppointmentInterval(settingsModel.getAppointmentInterval());
        settingsDTO.setMaxCancellationInterval(settingsModel.getMaxCancellationInterval());

        if (settingsModel.getServices() != null) {
            settingsDTO.setServices(
                    settingsModel.getServices().stream()
                            .map(serviceMapper::toResponseAllDetails)
                            .collect(Collectors.toList())
            );
        }

        if (settingsModel.getOperatingHours() != null) {
            settingsDTO.setOperatingHours(
                    settingsModel.getOperatingHours().stream()
                            .map(operatingHoursMapper::toResponseAllDetails)
                            .collect(Collectors.toList())
            );
        }

        if (settingsModel.getOffDays() != null) {
            settingsDTO.setOffDays(
                    settingsModel.getOffDays().stream()
                            .map(offDaysMapper::toResponseAllDetails)
                            .collect(Collectors.toList())
            );
        }

        return settingsDTO;
    }

    public SettingsModel toUpdateFromDTO(UpdateSettingsDTO updateSettingsDTO, SettingsModel settingsModel) {
        if (updateSettingsDTO.getAppointmentInterval() != null)
            settingsModel.setAppointmentInterval(updateSettingsDTO.getAppointmentInterval());

        if (updateSettingsDTO.getMaxCancellationInterval() != null)
            settingsModel.setMaxCancellationInterval(updateSettingsDTO.getMaxCancellationInterval());

        return settingsModel;
    }

    public UpdateSettingsDTO toUpdateSettings(SettingsModel settingsModel) {
        UpdateSettingsDTO updateSettingsDTO = new UpdateSettingsDTO();

        updateSettingsDTO.setAppointmentInterval(settingsModel.getAppointmentInterval());
        updateSettingsDTO.setMaxCancellationInterval(settingsModel.getMaxCancellationInterval());

        return updateSettingsDTO;
    }

}
