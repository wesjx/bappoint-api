package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.clerk.ClerkAuthContext;
import com.wesleysilva.bappoint.exceptions.CompanyDeleteException;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.EmailAlreadyExistsException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
    }

    @Transactional
    @PreAuthorize("hasRole('MASTER')")
    public CreateCompanyDTO createCompany(CreateCompanyDTO companyDTO) {
        if (companyRepository.existsByEmail(companyDTO.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        CompanyModel company = new CompanyModel();
        company.setName(companyDTO.getName());
        company.setEmail(companyDTO.getEmail());
        company.setPhone(companyDTO.getPhone());
        company.setAddress(companyDTO.getAddress());
        company.setStripeAccountId(companyDTO.getStripeAccountId());
        company.setClerkUserId(companyDTO.getClerkUserId());
        company.setSlug(companyDTO.getSlug());
        company.setClerkUserId(companyDTO.getClerkUserId());

        if (companyDTO.getSettings() != null) {
            SettingsModel settings = new SettingsModel();
            settings.setAppointmentInterval(companyDTO.getSettings().getAppointmentInterval());
            settings.setMaxCancellationInterval(companyDTO.getSettings().getMaxCancellationInterval());
            company.setSettings(settings);
        }

        assert companyDTO.getSettings() != null;
        if (companyDTO.getSettings().getMaxCancellationInterval() == null) {
            throw new IllegalArgumentException("Max Cancellation Interval cannot be null");
        }

        if (companyDTO.getSettings().getAppointmentInterval() == null) {
            throw new IllegalArgumentException("Appointment Interval cannot be null");
        }

        CompanyModel saved = companyRepository.save(company);

        return companyMapper.toCreate(saved);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('MASTER')")
    public List<CompanyResponseDTO> listCompanies() {
       List<CompanyModel> companyModel = companyRepository.findAll();
        return companyModel.stream()
                .map(companyMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isOwner(#id)")
    public CompanyDetailsResponseDTO getCompanyById(UUID id) {
        CompanyModel company = companyRepository.findById(id)
                .orElseThrow(CompanyNotFoundException::new);

        if (company.getAppointments() != null) {
            company.getAppointments().size();
        }
        return companyMapper.toDetailsResponseDTO(company);
    }

    @Transactional
    @PreAuthorize("hasRole('MASTER')")
    void deleteCompany(UUID companyId) {
        CompanyModel companyModel = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);

        try{
            companyRepository.delete(companyModel);
        } catch (Exception exception){
            throw new CompanyDeleteException();
        }
    }

    @Transactional
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isOwner(#companyId)")
    public CompanyResponseDTO updateCompany(UUID companyId, UpdateCompanyDTO updateDTO) {
        CompanyModel existingCompany = companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);
        CompanyModel updatedCompany = companyMapper.toUpdateCompany(updateDTO, existingCompany);
        updatedCompany = companyRepository.save(updatedCompany);
        return companyMapper.toResponseDTO(updatedCompany);
    }

}
