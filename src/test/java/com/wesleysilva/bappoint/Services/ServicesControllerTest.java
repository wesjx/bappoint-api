package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceResponseDTO;
import com.wesleysilva.bappoint.Services.dto.UpdateServiceDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicesControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServicesController servicesController;

    private UUID companyId;
    private UUID serviceId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // POST /create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create service and return 201")
    void createService_shouldReturn201WithBody() {
        CreateServiceDTO requestDTO = new CreateServiceDTO();
        CreateServiceDTO responseDTO = new CreateServiceDTO();

        when(serviceService.createService(any(CreateServiceDTO.class), eq(companyId)))
                .thenReturn(responseDTO);

        ResponseEntity<CreateServiceDTO> response =
                servicesController.createService(companyId, requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(serviceService).createService(requestDTO, companyId);
    }

    @Test
    @DisplayName("Should call createService exactly once")
    void createService_shouldInvokeServiceOnce() {
        when(serviceService.createService(any(CreateServiceDTO.class), any()))
                .thenReturn(new CreateServiceDTO());

        servicesController.createService(companyId, new CreateServiceDTO());

        verify(serviceService, times(1))
                .createService(any(CreateServiceDTO.class), eq(companyId));
        verifyNoMoreInteractions(serviceService);
    }

    // -------------------------------------------------------------------------
    // GET /list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of services with status 200")
    void listServices_shouldReturnList() {
        List<ServiceResponseDTO> services = List.of(
                new ServiceResponseDTO(),
                new ServiceResponseDTO()
        );

        when(serviceService.listAllServices(companyId)).thenReturn(services);

        ResponseEntity<List<ServiceResponseDTO>> response =
                servicesController.listServices(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(serviceService).listAllServices(companyId);
    }

    @Test
    @DisplayName("Should return empty list when no services exist for the company")
    void listServices_shouldReturnEmptyList() {
        when(serviceService.listAllServices(companyId)).thenReturn(List.of());

        ResponseEntity<List<ServiceResponseDTO>> response =
                servicesController.listServices(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /{serviceId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return service details by ID with status 200")
    void getServiceById_shouldReturnDetails() {
        ServiceAllDetailsDTO dto = new ServiceAllDetailsDTO();

        when(serviceService.getServiceById(serviceId)).thenReturn(dto);

        ResponseEntity<ServiceAllDetailsDTO> response =
                servicesController.getServiceById(serviceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(serviceService).getServiceById(serviceId);
    }

    // -------------------------------------------------------------------------
    // DELETE /delete/{serviceId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete service and return 200 with confirmation message")
    void deleteService_shouldReturn200WithMessage() {
        doNothing().when(serviceService).deleteService(serviceId);

        ResponseEntity<String> response =
                servicesController.deleteService(serviceId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Service deleted", response.getBody());
        verify(serviceService).deleteService(serviceId);
    }

    @Test
    @DisplayName("Should call deleteService exactly once with correct ID")
    void deleteService_shouldInvokeServiceOnce() {
        doNothing().when(serviceService).deleteService(any());

        servicesController.deleteService(serviceId);

        verify(serviceService, times(1)).deleteService(serviceId);
        verifyNoMoreInteractions(serviceService);
    }

    // -------------------------------------------------------------------------
    // PUT /update/{serviceId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update service and return 200 with updated body")
    void updateService_shouldReturn200WithBody() {
        CreateServiceDTO requestDTO = new CreateServiceDTO();
        UpdateServiceDTO responseDTO = new UpdateServiceDTO();

        when(serviceService.updateService(eq(serviceId), any(CreateServiceDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<UpdateServiceDTO> response =
                servicesController.updateService(serviceId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(serviceService).updateService(serviceId, requestDTO);
    }

    @Test
    @DisplayName("Should call updateService exactly once with correct arguments")
    void updateService_shouldInvokeServiceOnce() {
        CreateServiceDTO requestDTO = new CreateServiceDTO();

        when(serviceService.updateService(any(), any(CreateServiceDTO.class)))
                .thenReturn(new UpdateServiceDTO());

        servicesController.updateService(serviceId, requestDTO);

        verify(serviceService, times(1)).updateService(serviceId, requestDTO);
        verifyNoMoreInteractions(serviceService);
    }
}
