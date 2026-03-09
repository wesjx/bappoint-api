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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final ClerkAuthContext clerkAuthContext;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper, ClerkAuthContext clerkAuthContext) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.clerkAuthContext = clerkAuthContext;
    }

    @Transactional
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
    public List<CompanyResponseDTO> listCompanies() {
       List<CompanyModel> companyModel = companyRepository.findAll();
        if(clerkAuthContext.isMaster()){
            return companyModel.stream()
                    .map(companyMapper::toResponseDTO)
                    .collect(Collectors.toList());
        }

        String clerkUserId = clerkAuthContext.getCurrentClerkUserId();
        return companyRepository.findAllByClerkUserId(clerkUserId)
                .stream()
                .map(companyMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyDetailsResponseDTO getCompanyById(UUID id) {
        CompanyModel companyModel = companyRepository.findById(id).orElseThrow(CompanyNotFoundException::new);

        if (!clerkAuthContext.isMaster()) {
            String clerkUserId = clerkAuthContext.getCurrentClerkUserId();
            if(!companyModel.getClerkUserId().equals(clerkUserId)){
                throw new CompanyNotFoundException();
            }
        }

        return companyMapper.toDetailsResponseDTO(companyModel);
    }

    @Transactional
    void deleteCompany(UUID companyId) {
        CompanyModel companyModel = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);

        try{
            companyRepository.delete(companyModel);
        } catch (Exception exception){
            throw new CompanyDeleteException();
        }
    }

    @Transactional
    public CompanyResponseDTO updateCompany(UUID companyId, UpdateCompanyDTO updateDTO) {
        CompanyModel existingCompany = companyRepository.findById(companyId).orElseThrow(CompanyNotFoundException::new);

        if (!clerkAuthContext.isMaster()) {
            String clerkUserId = clerkAuthContext.getCurrentClerkUserId();
            if (!existingCompany.getClerkUserId().equals(clerkUserId)) {
                throw new CompanyNotFoundException();
            }
        }

        CompanyModel updatedCompany = companyMapper.toUpdateCompany(updateDTO, existingCompany);
        updatedCompany = companyRepository.save(updatedCompany);
        return companyMapper.toResponseDTO(updatedCompany);
    }

}
