package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceResponseDTO;
import com.wesleysilva.bappoint.Services.dto.UpdateServiceDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.ServiceNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;
    private final CompanyRepository companyRepository;

    public ServiceService(ServiceRepository serviceRepository, ServiceMapper serviceMapper, CompanyRepository companyRepository) {
        this.serviceRepository = serviceRepository;
        this.serviceMapper = serviceMapper;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CreateServiceDTO createService(CreateServiceDTO serviceDTO, UUID companyId) {
        ServiceModel serviceModel = serviceMapper.toEntity(serviceDTO);

        CompanyModel company = companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);

        SettingsModel settings = company.getSettings();

        if(settings == null) {
            throw new SettingsNotFoundException();
        }

        serviceModel.setSettings(settings);

        serviceModel = serviceRepository.save(serviceModel);

        return serviceMapper.toCreate(serviceModel);
    }

    @Transactional(readOnly = true)
    public List<ServiceResponseDTO> listAllServices() {
        List<ServiceModel> serviceModels = serviceRepository.findAll();
        return serviceModels.stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServiceAllDetailsDTO getServiceById(UUID serviceId) {
        ServiceModel serviceModel = serviceRepository
                .findById(serviceId)
                .orElseThrow(ServiceNotFoundException::new);
        return serviceMapper.toResponseAllDetails(serviceModel);
    }

    @Transactional
    public void deleteService(UUID serviceId) {
        ServiceModel service = serviceRepository.findById(serviceId)
                .orElseThrow(ServiceNotFoundException::new);
        serviceRepository.delete(service);
    }


    public UpdateServiceDTO updateService(UUID serviceId, CreateServiceDTO serviceDTO) {
        Optional<ServiceModel> existingService = Optional.of(serviceRepository.findById(serviceId)
                .orElseThrow(ServiceNotFoundException::new));

            ServiceModel serviceToUpdate = existingService.get();

            serviceToUpdate.setName(serviceDTO.getName());
            serviceToUpdate.setPrice(serviceDTO.getPrice());
            serviceToUpdate.setDurationMinutes(serviceDTO.getDurationMinutes());
            serviceToUpdate.setIsActive(serviceDTO.getIsActive());

            ServiceModel savedService = serviceRepository.save(serviceToUpdate);
            return serviceMapper.toUpdate(savedService);
    }
}
