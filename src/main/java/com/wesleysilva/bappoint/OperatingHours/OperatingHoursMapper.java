package com.wesleysilva.bappoint.OperatingHours;

import org.springframework.stereotype.Component;

@Component
public class OperatingHoursMapper {
    public OperatingHoursModel toEntity(OperatingHoursDTO operatingHoursDTO) {
        OperatingHoursModel operatingHoursModel = new OperatingHoursModel();

        operatingHoursModel.setId(operatingHoursDTO.getId());
        operatingHoursModel.setStart_date(operatingHoursDTO.getStart_date());
        operatingHoursModel.setEnd_date(operatingHoursDTO.getEnd_date());
        operatingHoursModel.setWeekday(operatingHoursDTO.getWeekday());
        operatingHoursModel.setIs_active(operatingHoursDTO.getIs_active());

        return operatingHoursModel;
    }

    public OperatingHoursDTO toDto(OperatingHoursModel operatingHoursModel) {
        OperatingHoursDTO operatingHoursDTO = new OperatingHoursDTO();

        operatingHoursDTO.setId(operatingHoursModel.getId());
        operatingHoursDTO.setStart_date(operatingHoursModel.getStart_date());
        operatingHoursDTO.setEnd_date(operatingHoursModel.getEnd_date());
        operatingHoursDTO.setWeekday(operatingHoursModel.getWeekday());
        operatingHoursDTO.setIs_active(operatingHoursModel.getIs_active());

        return operatingHoursDTO;
    }
}
