package com.wesleysilva.bappoint.offday;

import com.wesleysilva.bappoint.company.CompanyModel;
import com.wesleysilva.bappoint.company.CompanyRepository;
import com.wesleysilva.bappoint.offday.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.offday.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.offday.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.offday.dto.OffDaysResponseDTO;
import com.wesleysilva.bappoint.settings.SettingsModel;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.OffDayNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OffDaysService {

   private final OffDaysRepository offDaysRepository;
   private final OffDaysMapper offDaysMapper;
   private final CompanyRepository companyRepository;

    public OffDaysService(OffDaysRepository offDaysRepository, OffDaysMapper offDaysMapper, CompanyRepository companyRepository) {
        this.offDaysRepository = offDaysRepository;
        this.offDaysMapper = offDaysMapper;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CreateOffDayDTO createOffDays(UUID companyId, CreateOffDayDTO offDaysDTO) {
        OffDaysModel offDaysModel = offDaysMapper.toEntity(offDaysDTO);

        CompanyModel company = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);

        SettingsModel settings = company.getSettings();

        if(settings == null) {
            throw new SettingsNotFoundException();
        }

        offDaysModel.setSettings(settings);

        offDaysModel = offDaysRepository.save(offDaysModel);

        return offDaysMapper.toCreate(offDaysModel);
    }

    @Transactional(readOnly = true)
    public List<OffDaysResponseDTO> getAllOffDays(UUID companyId) {
        List<OffDaysModel> offDaysModels = offDaysRepository.findBySettingsCompanyId(companyId);
        return offDaysModels.stream()
                .map(offDaysMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OffDaysAllDetailsDTO getOffDaysById(UUID offDaysId) {
        OffDaysModel offDayById = offDaysRepository.findById(offDaysId)
                .orElseThrow(OffDayNotFoundException::new);
        return offDaysMapper.toResponseAllDetails(offDayById);
    }

    void deleteOffDaysById(UUID offDaysId) {
        OffDaysModel offDay = offDaysRepository.findById(offDaysId)
                        .orElseThrow(OffDayNotFoundException::new);

        offDaysRepository.delete(offDay);
    }

    @Transactional
    public OffDayUpdateDTO updateService(UUID offDaysID, OffDayUpdateDTO offDaysDTO) {
        return offDaysRepository.findById(offDaysID)
                .map(offDaysToUpdate -> {
                    offDaysToUpdate.setOffDaystype(offDaysDTO.getOffDaysType());
                    offDaysToUpdate.setReason(offDaysDTO.getReason());
                    offDaysToUpdate.setDate(offDaysDTO.getDate());
                    return offDaysMapper.toUpdate(offDaysRepository.save(offDaysToUpdate));
                })
                .orElseThrow(OffDayNotFoundException::new);
    }
}
