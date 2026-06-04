package com.wesleysilva.bappoint.offday;

import com.wesleysilva.bappoint.offday.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.offday.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.offday.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.offday.dto.OffDaysResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OffDaysMapper {
    public OffDaysModel toEntity(CreateOffDayDTO offDaysDTO) {
        OffDaysModel offDaysModel = new OffDaysModel();

        offDaysModel.setDate(offDaysDTO.getDate());
        offDaysModel.setReason(offDaysDTO.getReason());
        offDaysModel.setOffDaystype(offDaysDTO.getOffDaysType());

        return offDaysModel;
    }

    public CreateOffDayDTO toCreate(OffDaysModel offDaysModel) {
        CreateOffDayDTO offDaysDTO = new CreateOffDayDTO();

        offDaysDTO.setDate(offDaysModel.getDate());
        offDaysDTO.setReason(offDaysModel.getReason());
        offDaysDTO.setOffDaysType(offDaysModel.getOffDaystype());

        return offDaysDTO;
    }

    public OffDaysResponseDTO toResponse(OffDaysModel offDaysModel) {
        OffDaysResponseDTO offDaysDTO = new OffDaysResponseDTO();

        offDaysDTO.setDate(offDaysModel.getDate());
        offDaysDTO.setReason(offDaysModel.getReason());
        offDaysDTO.setOffDaysType(offDaysModel.getOffDaystype());

        return offDaysDTO;
    }

    public OffDaysAllDetailsDTO toResponseAllDetails(OffDaysModel offDaysModel) {
        OffDaysAllDetailsDTO offDaysDTO = new OffDaysAllDetailsDTO();

        offDaysDTO.setId(offDaysModel.getId());
        offDaysDTO.setDate(offDaysModel.getDate());
        offDaysDTO.setReason(offDaysModel.getReason());
        offDaysDTO.setOffDaysType(offDaysModel.getOffDaystype());

        return offDaysDTO;
    }

    public OffDayUpdateDTO toUpdate(OffDaysModel offDaysModel) {
        OffDayUpdateDTO offDaysDTO = new OffDayUpdateDTO();

        offDaysDTO.setDate(offDaysModel.getDate());
        offDaysDTO.setReason(offDaysModel.getReason());
        offDaysDTO.setOffDaysType(offDaysModel.getOffDaystype());

        return offDaysDTO;
    }

}
