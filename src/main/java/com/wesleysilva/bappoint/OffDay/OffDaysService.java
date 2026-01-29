package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.OperatingHours.OperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.OperatingHoursModel;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.SettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OffDaysService {

   private final OffDaysRepository offDaysRepository;
   private final OffDaysMapper offDaysMapper;
   private final SettingsRepository settingsRepository;

    public OffDaysService(OffDaysRepository offDaysRepository, OffDaysMapper offDaysMapper, SettingsRepository settingsRepository) {
        this.offDaysRepository = offDaysRepository;
        this.offDaysMapper = offDaysMapper;
        this.settingsRepository = settingsRepository;
    }

    public OffDaysDTO createOffDays(UUID settingsId, OffDaysDTO offDaysDTO) {
        OffDaysModel offDaysModel = offDaysMapper.toEntity(offDaysDTO);

        SettingsModel settings = settingsRepository
                .findById(settingsId)
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        offDaysModel.setSettings(settings);

        offDaysModel = offDaysRepository.save(offDaysModel);

        return offDaysMapper.toDto(offDaysModel);
    }

    public List<OffDaysDTO> getAllOffDays(){
        List<OffDaysModel> offDaysModels = offDaysRepository.findAll();

        return offDaysModels.stream()
                .map(offDaysMapper::toDto)
                .collect(Collectors.toList());
    }

    public OffDaysDTO getOffDaysById(UUID offDaysId) {
        OffDaysModel offDayById = offDaysRepository.findById(offDaysId).orElseThrow(()  -> new RuntimeException("OffDays not found"));
        return offDaysMapper.toDto(offDayById);
    }

    void deleteOffDaysById(UUID offDaysId) {
        offDaysRepository.deleteById(offDaysId);
    }

    public OffDaysDTO updateService(UUID offDaysID, OffDaysDTO offDaysDTO) {
        Optional<OffDaysModel> existingOffDays = offDaysRepository.findById(offDaysID);

        if (existingOffDays.isPresent()) {
            OffDaysModel offDaysToUpdate = existingOffDays.get();

            offDaysToUpdate.setOffDaystype(offDaysDTO.getOffDaysType());
            offDaysToUpdate.setReason(offDaysDTO.getReason());
            offDaysToUpdate.setDate(offDaysDTO.getDate());
            offDaysToUpdate.setOffDaystype(offDaysDTO.getOffDaysType());

            OffDaysModel savedOffDays = offDaysRepository.save(offDaysToUpdate);
            return offDaysMapper.toDto(savedOffDays);
        }

        return null;
    }
}
