package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.SettingsRepository;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.OffDayNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
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
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public List<OffDaysResponseDTO> getAllOffDays(){
        List<OffDaysModel> offDaysModels = offDaysRepository.findAll();
        return offDaysModels.stream()
                .map(offDaysMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOffDayOwner(#offDaysId)")
    public OffDaysAllDetailsDTO getOffDaysById(UUID offDaysId) {
        OffDaysModel offDayById = offDaysRepository.findById(offDaysId)
                .orElseThrow(OffDayNotFoundException::new);
        return offDaysMapper.toResponseAllDetails(offDayById);
    }

    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOffDayOwner(#offDaysId)")
    void deleteOffDaysById(UUID offDaysId) {
        OffDaysModel offDay = offDaysRepository.findById(offDaysId)
                        .orElseThrow(OffDayNotFoundException::new);

        offDaysRepository.delete(offDay);
    }

    @Transactional
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isOffDayOwner(#offDaysId)")
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
