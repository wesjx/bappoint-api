package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
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
class OperatingHoursControllerTest {

    @Mock
    private OperatingHoursService operatingHoursService;

    @InjectMocks
    private OperatingHoursController operatingHoursController;

    private UUID companyId;
    private UUID operatingHoursId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        operatingHoursId = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // POST /create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create operating hours and return 201")
    void createOperatingHours_shouldReturn201WithBody() {
        CreateOperatingHoursDTO requestDTO = new CreateOperatingHoursDTO();
        CreateOperatingHoursDTO responseDTO = new CreateOperatingHoursDTO();

        when(operatingHoursService.createOperatingHours(eq(companyId), any(CreateOperatingHoursDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<CreateOperatingHoursDTO> response =
                operatingHoursController.createOperatingHours(companyId, requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(operatingHoursService).createOperatingHours(companyId, requestDTO);
    }

    @Test
    @DisplayName("Should call createOperatingHours exactly once")
    void createOperatingHours_shouldInvokeServiceOnce() {
        when(operatingHoursService.createOperatingHours(any(), any(CreateOperatingHoursDTO.class)))
                .thenReturn(new CreateOperatingHoursDTO());

        operatingHoursController.createOperatingHours(companyId, new CreateOperatingHoursDTO());

        verify(operatingHoursService, times(1))
                .createOperatingHours(eq(companyId), any(CreateOperatingHoursDTO.class));
        verifyNoMoreInteractions(operatingHoursService);
    }

    // -------------------------------------------------------------------------
    // GET /list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of operating hours with status 200")
    void listOperatingHours_shouldReturnList() {
        List<OperatingHoursResponseDTO> operatingHours = List.of(
                new OperatingHoursResponseDTO(),
                new OperatingHoursResponseDTO()
        );

        when(operatingHoursService.getAllOperatingHours(companyId)).thenReturn(operatingHours);

        ResponseEntity<List<OperatingHoursResponseDTO>> response =
                operatingHoursController.listOperatingHours(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(operatingHoursService).getAllOperatingHours(companyId);
    }

    @Test
    @DisplayName("Should return empty list when no operating hours exist for the company")
    void listOperatingHours_shouldReturnEmptyList() {
        when(operatingHoursService.getAllOperatingHours(companyId)).thenReturn(List.of());

        ResponseEntity<List<OperatingHoursResponseDTO>> response =
                operatingHoursController.listOperatingHours(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /{operatingHoursId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return operating hours details by ID with status 200")
    void getOperatingHoursById_shouldReturnDetails() {
        OperatingHoursAllDetailsDTO dto = new OperatingHoursAllDetailsDTO();

        when(operatingHoursService.getOperatingHoursById(operatingHoursId)).thenReturn(dto);

        ResponseEntity<OperatingHoursAllDetailsDTO> response =
                operatingHoursController.getOperatingHoursById(operatingHoursId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody());
        verify(operatingHoursService).getOperatingHoursById(operatingHoursId);
    }

    // -------------------------------------------------------------------------
    // DELETE /{operatingHoursId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete operating hours and return 204 No Content")
    void deleteOperatingHoursById_shouldReturn204() {
        doNothing().when(operatingHoursService).deleteOperatingHoursById(operatingHoursId);

        ResponseEntity<Void> response =
                operatingHoursController.deleteOperatingHoursById(operatingHoursId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(operatingHoursService).deleteOperatingHoursById(operatingHoursId);
    }

    @Test
    @DisplayName("Should call deleteOperatingHoursById exactly once with correct ID")
    void deleteOperatingHoursById_shouldInvokeServiceOnce() {
        doNothing().when(operatingHoursService).deleteOperatingHoursById(any());

        operatingHoursController.deleteOperatingHoursById(operatingHoursId);

        verify(operatingHoursService, times(1)).deleteOperatingHoursById(operatingHoursId);
        verifyNoMoreInteractions(operatingHoursService);
    }

    // -------------------------------------------------------------------------
    // PUT /update/{operatingHoursId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update operating hours and return 200 with updated body")
    void updateOperatingHours_shouldReturn200WithBody() {
        UpdateOperatingHoursDTO requestDTO = new UpdateOperatingHoursDTO();
        UpdateOperatingHoursDTO responseDTO = new UpdateOperatingHoursDTO();

        when(operatingHoursService.updateService(eq(operatingHoursId), any(UpdateOperatingHoursDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<UpdateOperatingHoursDTO> response =
                operatingHoursController.updateOperatingHours(operatingHoursId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(operatingHoursService).updateService(operatingHoursId, requestDTO);
    }

    @Test
    @DisplayName("Should call updateService exactly once with correct arguments")
    void updateOperatingHours_shouldInvokeServiceOnce() {
        UpdateOperatingHoursDTO requestDTO = new UpdateOperatingHoursDTO();

        when(operatingHoursService.updateService(any(), any(UpdateOperatingHoursDTO.class)))
                .thenReturn(new UpdateOperatingHoursDTO());

        operatingHoursController.updateOperatingHours(operatingHoursId, requestDTO);

        verify(operatingHoursService, times(1)).updateService(operatingHoursId, requestDTO);
        verifyNoMoreInteractions(operatingHoursService);
    }
}
