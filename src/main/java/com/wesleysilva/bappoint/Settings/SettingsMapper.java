package com.wesleysilva.bappoint.Settings;

import com.wesleysilva.bappoint.OffDay.OffDaysMapper;
import com.wesleysilva.bappoint.OffDay.OffDaysModel;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursMapper;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.Services.ServiceMapper;
import com.wesleysilva.bappoint.Services.ServiceModel;
import org.springframework.stereotype.Component;

import java.util.List;
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

    public SettingsModel map(SettingsDTO settingsDTO) {
        SettingsModel settingsModel = new SettingsModel();
        settingsModel.setId(settingsDTO.getId());
        settingsModel.setAppointment_interval(settingsDTO.getAppointment_interval());
        settingsModel.setMax_cancellation_interval(settingsDTO.getMax_cancellation_interval());

        if (settingsDTO.getServices() != null) {
            List<ServiceModel> serviceModels = settingsDTO.getServices()
                    .stream()
                    .map(serviceMapper::toEntity)
                    .peek(service -> service.setSettings(settingsModel))
                    .collect(Collectors.toList());
            settingsModel.setServices(serviceModels);
        }

        if (settingsDTO.getOperating_hours() != null) {
            List<OperatingHoursModel> operatingHoursModels = settingsDTO.getOperating_hours()
                    .stream()
                    .map(operatingHoursMapper::toEntity)
                    .peek(operatingHours -> operatingHours.setSettings(settingsModel))
                    .collect(Collectors.toList());
            settingsModel.setOperatingHours(operatingHoursModels);
        }

        if (settingsDTO.getOff_days() != null) {
            List<OffDaysModel> offDaysModel = settingsDTO.getOff_days()
                    .stream()
                    .map(offDaysMapper::toEntity)
                    .peek(offDays -> offDays.setSettings(settingsModel))
                    .collect(Collectors.toList());
            settingsModel.setOffDays(offDaysModel);
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
                            .map(serviceMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        if (settingsModel.getOperatingHours() != null) {
            settingsDTO.setOperating_hours(
                    settingsModel.getOperatingHours().stream()
                            .map(operatingHoursMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        if (settingsModel.getOffDays() != null) {
            settingsDTO.setOff_days(
                    settingsModel.getOffDays().stream()
                            .map(offDaysMapper::toDto)
                            .collect(Collectors.toList())
            );
        }

        return settingsDTO;
    }
}
