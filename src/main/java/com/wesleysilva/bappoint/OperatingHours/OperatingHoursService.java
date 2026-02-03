package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.Services.ServiceDTO;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.SettingsRepository;
import com.wesleysilva.bappoint.enums.WeekDay;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    public OperatingHoursDTO updateService(UUID operatingHoursID, OperatingHoursDTO operatingHoursDTO) {
        Optional<OperatingHoursModel> existingOperatingHours = operatingHoursRepository.findById(operatingHoursID);

        if (existingOperatingHours.isPresent()) {
            OperatingHoursModel operatingHoursToUpdate = existingOperatingHours.get();

            operatingHoursToUpdate.setWeekday(operatingHoursDTO.getWeekday());
            operatingHoursToUpdate.setStart_date(operatingHoursDTO.getStart_date());
            operatingHoursToUpdate.setEnd_date(operatingHoursDTO.getEnd_date());
            operatingHoursToUpdate.setIs_active(operatingHoursDTO.getIs_active());

            OperatingHoursModel savedOperatingHours = operatingHoursRepository.save(operatingHoursToUpdate);
            return operatingHoursMapper.toDto(savedOperatingHours);
        }

        return null;
    }

    public OperatingHoursDTO findByDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        WeekDay weekday = WeekDay.valueOf(dayOfWeek.name());

        List<OperatingHoursModel> hours = operatingHoursRepository.findByWeekday(weekday);

        if (hours.isEmpty()) {
            return null;
        }

        OperatingHoursModel operatingHours = hours.getFirst();

        return operatingHoursMapper.toDto(operatingHours);
    }

}
