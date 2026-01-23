package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final CompanyRepository companyRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository, CompanyRepository companyRepository, ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.companyRepository = companyRepository;
        this.serviceMapper = serviceMapper;
    }

    public ServiceDTO createService(ServiceDTO serviceDTO) {
        ServiceModel serviceModel = serviceMapper.map(serviceDTO);

        CompanyModel companyId = companyRepository
                .findById(serviceDTO.getCompany_id())
                .orElseThrow(() -> new RuntimeException("Company not found")); //To get settings ID

        serviceModel.setCompany(companyId); //Set ID of settings into the service json

        serviceModel = serviceRepository.save(serviceModel);

        return serviceMapper.map(serviceModel);
    }

    public List<ServiceDTO> listServices() {
        List<ServiceModel> serviceModels = serviceRepository.findAll();

        return serviceModels.stream()
                .map(serviceMapper::map)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(UUID id) {
        ServiceModel serviceModel = serviceRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        return serviceMapper.map(serviceModel);
    }

    void deleteService(UUID id){
        serviceRepository.deleteById(id);
    }

    public ServiceDTO updateService(UUID id, ServiceDTO serviceDTO) {
        Optional<ServiceModel> serviceModel = serviceRepository.findById(id);
        if (serviceModel.isPresent()) {
            ServiceModel serviceUpdated = serviceMapper.map(serviceDTO);
            serviceUpdated.setId(id);
            ServiceModel serviceModelUpdated = serviceRepository.save(serviceUpdated);
            return serviceMapper.map(serviceModelUpdated);
        }
        return null;
    }

}
