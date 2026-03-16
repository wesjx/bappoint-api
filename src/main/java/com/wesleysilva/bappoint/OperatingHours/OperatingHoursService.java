package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.ExistsWeekDayException;
import com.wesleysilva.bappoint.exceptions.OperatingHoursNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OperatingHoursService {

    private final OperatingHoursRepository operatingHoursRepository;
    private final OperatingHoursMapper operatingHoursMapper;
    private final CompanyRepository companyRepository;

    public OperatingHoursService(OperatingHoursRepository operatingHoursRepository, OperatingHoursMapper operatingHoursMapper, CompanyRepository companyRepository) {
        this.operatingHoursRepository = operatingHoursRepository;
        this.operatingHoursMapper = operatingHoursMapper;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CreateOperatingHoursDTO createOperatingHours(UUID companyId, CreateOperatingHoursDTO operatingHoursDTO) {
        OperatingHoursModel operatingHoursModel = operatingHoursMapper.toEntity(operatingHoursDTO);

        CompanyModel companyModel = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);

        SettingsModel settings = companyModel.getSettings();

        if (settings == null) {
            throw new SettingsNotFoundException();
        }

        boolean exists = operatingHoursRepository
                .findBySettingsAndWeekday(companyModel.getSettings(), operatingHoursDTO.getWeekday())
                .isPresent();

        if(exists) {
            throw new ExistsWeekDayException();
        }

        operatingHoursModel.setSettings(settings);

        operatingHoursModel = operatingHoursRepository.save(operatingHoursModel);

        return operatingHoursMapper.toCreate(operatingHoursModel);
    }

    public List<OperatingHoursResponseDTO> getAllOperatingHours(UUID companyId) {
        return operatingHoursRepository.findBySettingsCompanyId(companyId)
                .stream()
                .map(operatingHoursMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OperatingHoursAllDetailsDTO getOperatingHoursById(UUID operatingHoursId) {
        OperatingHoursModel operatingHours = operatingHoursRepository.findById(operatingHoursId)
                .orElseThrow(OperatingHoursNotFoundException::new);
        return operatingHoursMapper.toResponseAllDetails(operatingHours);
    }

    @Transactional
    public void deleteOperatingHoursById(UUID operatingHoursId) {
        OperatingHoursModel operatingHoursModel = operatingHoursRepository.findById(operatingHoursId)
                        .orElseThrow(OperatingHoursNotFoundException::new);
        try{
        operatingHoursRepository.delete(operatingHoursModel);
        } catch(Exception e){
            throw new OperatingHoursNotFoundException();
        }
    }

    @Transactional
    public UpdateOperatingHoursDTO updateService(UUID operatingHoursID, UpdateOperatingHoursDTO operatingHoursDTO) {
        Optional<OperatingHoursModel> existingOperatingHours = Optional.of(operatingHoursRepository.findById(operatingHoursID)
                .orElseThrow(OperatingHoursNotFoundException::new));

        OperatingHoursModel operatingHoursToUpdate = existingOperatingHours.get();

        operatingHoursToUpdate.setWeekday(operatingHoursDTO.getWeekday());
        operatingHoursToUpdate.setStartTime(operatingHoursDTO.getStartTime());
        operatingHoursToUpdate.setEndTime(operatingHoursDTO.getEndTime());
        operatingHoursToUpdate.setIsActive(operatingHoursDTO.getIsActive());

        OperatingHoursModel savedOperatingHours = operatingHoursRepository.save(operatingHoursToUpdate);
        return operatingHoursMapper.toUpdate(savedOperatingHours);

    }

}
