package com.wesleysilva.bappoint.Services;

import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceModel toEntityWithoutSettings(ServiceDTO serviceDTO) {
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setId(serviceDTO.getId());
        serviceModel.setName(serviceDTO.getName());
        serviceModel.setPrice(serviceDTO.getPrice());
        serviceModel.setDuration_minutes(serviceDTO.getDuration_minutes());
        serviceModel.setIs_active(serviceDTO.getIs_active());
        return serviceModel;
    }

    public ServiceDTO map(ServiceModel serviceModel) {
        ServiceDTO serviceDTO = new ServiceDTO();
        serviceDTO.setId(serviceModel.getId());
        serviceDTO.setName(serviceModel.getName());
        serviceDTO.setPrice(serviceModel.getPrice());
        serviceDTO.setDuration_minutes(serviceModel.getDuration_minutes());
        serviceDTO.setIs_active(serviceModel.getIs_active());
        return serviceDTO;
    }
}
