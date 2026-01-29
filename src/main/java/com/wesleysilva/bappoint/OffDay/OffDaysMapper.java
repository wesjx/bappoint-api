package com.wesleysilva.bappoint.OffDay;

import org.springframework.stereotype.Component;

@Component
public class OffDaysMapper {
    public OffDaysModel toEntity(OffDaysDTO offDaysDTO) {
        OffDaysModel offDaysModel = new OffDaysModel();

        offDaysModel.setId(offDaysDTO.getId());
        offDaysModel.setDate(offDaysDTO.getDate());
        offDaysModel.setReason(offDaysDTO.getReason());
        offDaysModel.setOffDaystype(offDaysDTO.getOffDaysType());

        return offDaysModel;
    }

    public OffDaysDTO toDto(OffDaysModel offDaysModel) {
        OffDaysDTO offDaysDTO = new OffDaysDTO();

        offDaysDTO.setId(offDaysModel.getId());
        offDaysDTO.setDate(offDaysModel.getDate());
        offDaysDTO.setReason(offDaysModel.getReason());
        offDaysDTO.setOffDaysType(offDaysModel.getOffDaystype());

        return offDaysDTO;
    }
}
