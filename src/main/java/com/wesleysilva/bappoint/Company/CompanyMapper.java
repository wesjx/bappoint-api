package com.wesleysilva.bappoint.Company;

import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyModel map(CompanyDTO companyDTO) {
        CompanyModel companyModel = new CompanyModel();

        companyModel.setId(companyDTO.getId());
        companyModel.setName(companyDTO.getName());
        companyModel.setEmail(companyDTO.getEmail());
        companyModel.setPhone(companyDTO.getPhone());
        companyModel.setAddress(companyDTO.getAddress());

        return companyModel;
    }

    public CompanyDTO map(CompanyModel companyModel) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setId(companyModel.getId());
        companyDTO.setName(companyModel.getName());
        companyDTO.setEmail(companyModel.getEmail());
        companyDTO.setPhone(companyModel.getPhone());
        companyDTO.setAddress(companyModel.getAddress());

        return companyDTO;
    }
}
