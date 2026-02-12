package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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


    public CompanyResponseDTO createCompany(CreateCompanyDTO createCompanyDTO) {
        CompanyModel companyModel = companyMapper.toCreate(createCompanyDTO);
        companyModel = companyRepository.save(companyModel);

        return companyMapper.toResponseDTO(companyModel);
    }

    @Transactional(readOnly = true)
    public List<CompanyResponseDTO> listCompanies() {
       List<CompanyModel> companyModel = companyRepository.findAll();
        return companyModel.stream()
                .map(companyMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompanyById(UUID id) {
        CompanyModel companyModel = companyRepository.findById(id).orElse(null);
        assert companyModel != null;
        if (companyModel.getAppointments() != null) {
            companyModel.getAppointments().size();
        }
        return companyMapper.toResponseDTO(companyModel);
    }

    void deleteCompany(UUID id) {
        companyRepository.deleteById(id);
    }

    public CompanyResponseDTO updateCompany(UUID companyId, UpdateCompanyDTO updateDTO) {
        CompanyModel existingCompany = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        CompanyModel updatedCompany = companyMapper.toUpdateCompany(updateDTO, existingCompany);
        updatedCompany = companyRepository.save(updatedCompany);
        return companyMapper.toResponseDTO(updatedCompany);
    }

}
