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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock private ServiceRepository serviceRepository;
    @Mock private ServiceMapper serviceMapper;
    @Mock private CompanyRepository companyRepository;

    @InjectMocks
    private ServiceService serviceService;

    private UUID companyId;
    private UUID serviceId;
    private CompanyModel company;
    private SettingsModel settings;
    private ServiceModel serviceModel;
    private CreateServiceDTO createDTO;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        serviceId = UUID.randomUUID();

        settings = new SettingsModel();
        settings.setId(UUID.randomUUID());

        company = new CompanyModel();
        company.setId(companyId);
        company.setSettings(settings);

        serviceModel = new ServiceModel();
        serviceModel.setId(serviceId);

        createDTO = new CreateServiceDTO();
        createDTO.setName("Haircut");
        createDTO.setPrice(new BigDecimal("35.00"));
        createDTO.setDurationMinutes(30);
        createDTO.setIsActive(true);
    }

    // -------------------------------------------------------------------------
    // createService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create service successfully and return DTO")
    void createService_shouldCreateSuccessfully() {
        CreateServiceDTO responseDTO = new CreateServiceDTO();

        when(serviceMapper.toEntity(createDTO)).thenReturn(serviceModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(serviceRepository.save(serviceModel)).thenReturn(serviceModel);
        when(serviceMapper.toCreate(serviceModel)).thenReturn(responseDTO);

        CreateServiceDTO result = serviceService.createService(createDTO, companyId);

        assertNotNull(result);

        // Ensure settings were linked to the service before saving
        assertEquals(settings, serviceModel.getSettings());

        verify(serviceRepository).save(serviceModel);
        verify(serviceMapper).toCreate(serviceModel);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist")
    void createService_shouldThrowWhenCompanyNotFound() {
        when(serviceMapper.toEntity(createDTO)).thenReturn(serviceModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                serviceService.createService(createDTO, companyId)
        );

        verify(serviceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw SettingsNotFoundException when company has no settings")
    void createService_shouldThrowWhenSettingsIsNull() {
        company.setSettings(null);

        when(serviceMapper.toEntity(createDTO)).thenReturn(serviceModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(SettingsNotFoundException.class, () ->
                serviceService.createService(createDTO, companyId)
        );

        verify(serviceRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // listAllServices
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of all services for the company")
    void listAllServices_shouldReturnList() {
        ServiceModel second = new ServiceModel();
        ServiceResponseDTO dto1 = new ServiceResponseDTO();
        ServiceResponseDTO dto2 = new ServiceResponseDTO();

        when(serviceRepository.findBySettingsCompanyId(companyId))
                .thenReturn(List.of(serviceModel, second));
        when(serviceMapper.toResponse(serviceModel)).thenReturn(dto1);
        when(serviceMapper.toResponse(second)).thenReturn(dto2);

        List<ServiceResponseDTO> result = serviceService.listAllServices(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(serviceRepository).findBySettingsCompanyId(companyId);
        verify(serviceMapper, times(2)).toResponse(any(ServiceModel.class));
    }

    @Test
    @DisplayName("Should return empty list when no services exist for the company")
    void listAllServices_shouldReturnEmptyList() {
        when(serviceRepository.findBySettingsCompanyId(companyId)).thenReturn(List.of());

        List<ServiceResponseDTO> result = serviceService.listAllServices(companyId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(serviceMapper);
    }

    // -------------------------------------------------------------------------
    // getServiceById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return service details when found by ID")
    void getServiceById_shouldReturnDetails() {
        ServiceAllDetailsDTO detailsDTO = new ServiceAllDetailsDTO();

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceModel));
        when(serviceMapper.toResponseAllDetails(serviceModel)).thenReturn(detailsDTO);

        ServiceAllDetailsDTO result = serviceService.getServiceById(serviceId);

        assertNotNull(result);
        verify(serviceRepository).findById(serviceId);
        verify(serviceMapper).toResponseAllDetails(serviceModel);
    }

    @Test
    @DisplayName("Should throw ServiceNotFoundException when service does not exist")
    void getServiceById_shouldThrowWhenNotFound() {
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () ->
                serviceService.getServiceById(serviceId)
        );

        verifyNoInteractions(serviceMapper);
    }

    // -------------------------------------------------------------------------
    // deleteService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete service successfully when it exists")
    void deleteService_shouldDeleteSuccessfully() {
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceModel));
        doNothing().when(serviceRepository).delete(serviceModel);

        assertDoesNotThrow(() -> serviceService.deleteService(serviceId));

        verify(serviceRepository).findById(serviceId);
        verify(serviceRepository).delete(serviceModel);
    }

    @Test
    @DisplayName("Should throw ServiceNotFoundException when service does not exist on delete")
    void deleteService_shouldThrowWhenNotFound() {
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () ->
                serviceService.deleteService(serviceId)
        );

        verify(serviceRepository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // updateService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update service fields and return updated DTO")
    void updateService_shouldUpdateSuccessfully() {
        CreateServiceDTO updateDTO = new CreateServiceDTO();
        updateDTO.setName("Premium Haircut");
        updateDTO.setPrice(new BigDecimal("60.00"));
        updateDTO.setDurationMinutes(60);
        updateDTO.setIsActive(false);

        UpdateServiceDTO responseDTO = new UpdateServiceDTO();

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(serviceModel));
        when(serviceRepository.save(serviceModel)).thenReturn(serviceModel);
        when(serviceMapper.toUpdate(serviceModel)).thenReturn(responseDTO);

        UpdateServiceDTO result = serviceService.updateService(serviceId, updateDTO);

        assertNotNull(result);

        // Verify all fields were mutated on the model before saving
        assertEquals("Premium Haircut", serviceModel.getName());
        assertEquals(new BigDecimal("60.00"), serviceModel.getPrice());
        assertEquals(60, serviceModel.getDurationMinutes());
        assertFalse(serviceModel.getIsActive());

        verify(serviceRepository).save(serviceModel);
        verify(serviceMapper).toUpdate(serviceModel);
    }

    @Test
    @DisplayName("Should throw ServiceNotFoundException when service does not exist on update")
    void updateService_shouldThrowWhenNotFound() {
        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ServiceNotFoundException.class, () ->
                serviceService.updateService(serviceId, new CreateServiceDTO())
        );

        verify(serviceRepository, never()).save(any());
        verifyNoInteractions(serviceMapper);
    }
}
