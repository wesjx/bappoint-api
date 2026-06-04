package com.wesleysilva.bappoint.operatinghours;

import com.wesleysilva.bappoint.operatinghours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.operatinghours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.operatinghours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.operatinghours.dto.UpdateOperatingHoursDTO;
import org.springframework.stereotype.Component;

@Component
public class OperatingHoursMapper {
    public OperatingHoursModel toEntity(CreateOperatingHoursDTO operatingHoursDTO) {
        OperatingHoursModel operatingHoursModel = new OperatingHoursModel();

        operatingHoursModel.setStartTime(operatingHoursDTO.getStartTime());
        operatingHoursModel.setEndTime(operatingHoursDTO.getEndTime());
        operatingHoursModel.setLunchStartTime(operatingHoursDTO.getLunchStartTime());
        operatingHoursModel.setLunchEndTime(operatingHoursDTO.getLunchEndTime());
        operatingHoursModel.setWeekday(operatingHoursDTO.getWeekday());
        operatingHoursModel.setIsActive(operatingHoursDTO.getIsActive());

        return operatingHoursModel;
    }

    public CreateOperatingHoursDTO toCreate(OperatingHoursModel operatingHoursModel) {
        CreateOperatingHoursDTO operatingHoursDTO = new CreateOperatingHoursDTO();

        operatingHoursDTO.setId(operatingHoursModel.getId());
        operatingHoursDTO.setStartTime(operatingHoursModel.getStartTime());
        operatingHoursDTO.setEndTime(operatingHoursModel.getEndTime());
        operatingHoursDTO.setLunchStartTime(operatingHoursModel.getLunchStartTime());
        operatingHoursDTO.setLunchEndTime(operatingHoursModel.getLunchEndTime());
        operatingHoursDTO.setWeekday(operatingHoursModel.getWeekday());
        operatingHoursDTO.setIsActive(operatingHoursModel.getIsActive());

        return operatingHoursDTO;
    }

    public OperatingHoursResponseDTO toResponse(OperatingHoursModel operatingHoursModel) {
        OperatingHoursResponseDTO operatingHoursResponse = new OperatingHoursResponseDTO();

        operatingHoursResponse.setStartTime(operatingHoursModel.getStartTime());
        operatingHoursResponse.setEndTime(operatingHoursModel.getEndTime());
        operatingHoursResponse.setLunchStartTime(operatingHoursModel.getLunchStartTime());
        operatingHoursResponse.setLunchEndTime(operatingHoursModel.getLunchEndTime());
        operatingHoursResponse.setWeekday(operatingHoursModel.getWeekday());
        operatingHoursResponse.setIsActive(operatingHoursModel.getIsActive());

        return operatingHoursResponse;
    }

    public OperatingHoursAllDetailsDTO toResponseAllDetails(OperatingHoursModel operatingHoursModel) {
        OperatingHoursAllDetailsDTO operatingHoursDTO = new OperatingHoursAllDetailsDTO();

        operatingHoursDTO.setId(operatingHoursModel.getId());
        operatingHoursDTO.setStartTime(operatingHoursModel.getStartTime());
        operatingHoursDTO.setEndTime(operatingHoursModel.getEndTime());
        operatingHoursDTO.setLunchStartTime(operatingHoursModel.getLunchStartTime());
        operatingHoursDTO.setLunchEndTime(operatingHoursModel.getLunchEndTime());
        operatingHoursDTO.setWeekday(operatingHoursModel.getWeekday());
        operatingHoursDTO.setIsActive(operatingHoursModel.getIsActive());

        return operatingHoursDTO;
    }

    public UpdateOperatingHoursDTO toUpdate(OperatingHoursModel operatingHoursModel) {
        UpdateOperatingHoursDTO operatingHoursDTO = new UpdateOperatingHoursDTO();

        operatingHoursDTO.setStartTime(operatingHoursModel.getStartTime());
        operatingHoursDTO.setEndTime(operatingHoursModel.getEndTime());
        operatingHoursDTO.setLunchStartTime(operatingHoursModel.getLunchStartTime());
        operatingHoursDTO.setLunchEndTime(operatingHoursModel.getLunchEndTime());
        operatingHoursDTO.setWeekday(operatingHoursModel.getWeekday());
        operatingHoursDTO.setIsActive(operatingHoursModel.getIsActive());

        return operatingHoursDTO;
    }
}
