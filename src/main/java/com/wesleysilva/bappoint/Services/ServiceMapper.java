package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceResponseDTO;
import com.wesleysilva.bappoint.Services.dto.UpdateServiceDTO;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public ServiceModel toEntity(CreateServiceDTO serviceDTO) {
        ServiceModel serviceModel = new ServiceModel();
        serviceModel.setId(serviceDTO.getId());
        serviceModel.setName(serviceDTO.getName());
        serviceModel.setPrice(serviceDTO.getPrice());
        serviceModel.setDurationMinutes(serviceDTO.getDurationMinutes());
        serviceModel.setIsActive(serviceDTO.getIsActive());
        return serviceModel;
    }

    public CreateServiceDTO toCreate(ServiceModel serviceModel) {
        CreateServiceDTO serviceDTO = new CreateServiceDTO();
        serviceDTO.setId(serviceModel.getId());
        serviceDTO.setName(serviceModel.getName());
        serviceDTO.setPrice(serviceModel.getPrice());
        serviceDTO.setDurationMinutes(serviceModel.getDurationMinutes());
        serviceDTO.setIsActive(serviceModel.getIsActive());
        return serviceDTO;
    }

    public ServiceResponseDTO toResponse(ServiceModel serviceModel) {
        ServiceResponseDTO serviceDTO = new ServiceResponseDTO();

        serviceDTO.setName(serviceModel.getName());
        serviceDTO.setPrice(serviceModel.getPrice());
        serviceDTO.setDurationMinutes(serviceModel.getDurationMinutes());
        serviceDTO.setIsActive(serviceModel.getIsActive());

        return serviceDTO;
    }

    public ServiceAllDetailsDTO toResponseAllDetails(ServiceModel serviceModel) {
        ServiceAllDetailsDTO serviceDTO = new ServiceAllDetailsDTO();
        serviceDTO.setId(serviceModel.getId());
        serviceDTO.setName(serviceModel.getName());
        serviceDTO.setPrice(serviceModel.getPrice());
        serviceDTO.setDurationMinutes(serviceModel.getDurationMinutes());
        serviceDTO.setIsActive(serviceModel.getIsActive());
        return serviceDTO;
    }

    public UpdateServiceDTO toUpdate(ServiceModel serviceModel) {
        UpdateServiceDTO serviceDTO = new UpdateServiceDTO();

        serviceDTO.setName(serviceModel.getName());
        serviceDTO.setPrice(serviceModel.getPrice());
        serviceDTO.setDurationMinutes(serviceModel.getDurationMinutes());
        serviceDTO.setIsActive(serviceModel.getIsActive());
        return serviceDTO;
    }
}
