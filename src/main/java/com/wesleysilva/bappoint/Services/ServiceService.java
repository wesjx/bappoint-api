package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.SettingsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final SettingsRepository settingsRepository;
    private final ServiceMapper serviceMapper;

    public ServiceService(ServiceRepository serviceRepository,
                          SettingsRepository settingsRepository,
                          ServiceMapper serviceMapper) {
        this.serviceRepository = serviceRepository;
        this.settingsRepository = settingsRepository;
        this.serviceMapper = serviceMapper;
    }

    public ServiceDTO createService(UUID settingsId, ServiceDTO serviceDTO) {

        ServiceModel serviceModel = serviceMapper.toEntityWithoutSettings(serviceDTO);

        SettingsModel settings = settingsRepository
                .findById(settingsId)
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        serviceModel.setSettings(settings);

        serviceModel = serviceRepository.save(serviceModel);

        return serviceMapper.map(serviceModel);
    }

    public List<ServiceDTO> listServicesBySettings(UUID settingsId) {
        List<ServiceModel> serviceModels = serviceRepository.findBySettingsId(settingsId);
        return serviceModels.stream()
                .map(serviceMapper::map)
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> listAllServices() {
        List<ServiceModel> serviceModels = serviceRepository.findAll();
        return serviceModels.stream()
                .map(serviceMapper::map)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(UUID serviceId) {
        ServiceModel serviceModel = serviceRepository
                .findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return serviceMapper.map(serviceModel);
    }

    public void deleteService(UUID serviceId) {
        serviceRepository.deleteById(serviceId);
    }

    public ServiceDTO updateService(UUID serviceId, ServiceDTO serviceDTO) {
        Optional<ServiceModel> existingService = serviceRepository.findById(serviceId);

        if (existingService.isPresent()) {
            ServiceModel serviceToUpdate = existingService.get();

            serviceToUpdate.setName(serviceDTO.getName());
            serviceToUpdate.setPrice(serviceDTO.getPrice());
            serviceToUpdate.setDuration_minutes(serviceDTO.getDuration_minutes());
            serviceToUpdate.setIs_active(serviceDTO.getIs_active());

            ServiceModel savedService = serviceRepository.save(serviceToUpdate);
            return serviceMapper.map(savedService);
        }

        return null;
    }
}
