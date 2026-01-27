package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.SettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OperatingHoursService {

    private final OperatingHoursRepository operatingHoursRepository;
    private final OperatingHoursMapper operatingHoursMapper;
    private final SettingsRepository settingsRepository;

    public OperatingHoursService(OperatingHoursRepository operatingHoursRepository, OperatingHoursMapper operatingHoursMapper, SettingsRepository settingsRepository) {
        this.operatingHoursRepository = operatingHoursRepository;
        this.operatingHoursMapper = operatingHoursMapper;
        this.settingsRepository = settingsRepository;
    }

    public OperatingHoursDTO createOperatingHours(UUID settingsId, OperatingHoursDTO operatingHoursDTO) {
        OperatingHoursModel operatingHoursModel = operatingHoursMapper.toEntity(operatingHoursDTO);

        SettingsModel settings = settingsRepository
                .findById(settingsId)
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        operatingHoursModel.setSettings(settings);

        operatingHoursModel = operatingHoursRepository.save(operatingHoursModel);

        return operatingHoursMapper.toDto(operatingHoursModel);
    }

    public List<OperatingHoursDTO> getAllOperatingHours() {
        List<OperatingHoursModel> operatingHoursModels = operatingHoursRepository.findAll();
        return operatingHoursModels.stream()
                .map(operatingHoursMapper::toDto)
                .collect(Collectors.toList());
    }

    public OperatingHoursDTO getOperatingHoursById(UUID operatingHoursId) {
        OperatingHoursModel operatingHours = operatingHoursRepository.findById(operatingHoursId)
                .orElseThrow(() -> new RuntimeException("Company or settings not found"));

        return operatingHoursMapper.toDto(operatingHours);
    }

    public void deleteOperatingHoursById(UUID operatingHoursId) {
        operatingHoursRepository.deleteById(operatingHoursId);
    }

}
