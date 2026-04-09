package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
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
class OffDaysControllerTest {

    @Mock
    private OffDaysService offDaysService;

    @InjectMocks
    private OffDaysController offDaysController;

    private UUID companyId;
    private UUID offDaysId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        offDaysId = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // POST /create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create off day and return 201")
    void createOffDays_shouldReturn201WithBody() {
        CreateOffDayDTO requestDTO = new CreateOffDayDTO();
        CreateOffDayDTO responseDTO = new CreateOffDayDTO();

        when(offDaysService.createOffDays(eq(companyId), any(CreateOffDayDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<CreateOffDayDTO> response =
                offDaysController.createOffDays(companyId, requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(offDaysService).createOffDays(companyId, requestDTO);
    }

    @Test
    @DisplayName("Should call createOffDays exactly once")
    void createOffDays_shouldInvokeServiceOnce() {
        when(offDaysService.createOffDays(any(), any(CreateOffDayDTO.class)))
                .thenReturn(new CreateOffDayDTO());

        offDaysController.createOffDays(companyId, new CreateOffDayDTO());

        verify(offDaysService, times(1)).createOffDays(eq(companyId), any(CreateOffDayDTO.class));
        verifyNoMoreInteractions(offDaysService);
    }

    // -------------------------------------------------------------------------
    // GET /list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of off days with status 200")
    void getAllOffDays_shouldReturnList() {
        List<OffDaysResponseDTO> offDays = List.of(
                new OffDaysResponseDTO(),
                new OffDaysResponseDTO()
        );

        when(offDaysService.getAllOffDays(companyId)).thenReturn(offDays);

        ResponseEntity<List<OffDaysResponseDTO>> response =
                offDaysController.getAllOffDays(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(offDaysService).getAllOffDays(companyId);
    }

    @Test
    @DisplayName("Should return empty list when no off days exist for the company")
    void getAllOffDays_shouldReturnEmptyList() {
        when(offDaysService.getAllOffDays(companyId)).thenReturn(List.of());

        ResponseEntity<List<OffDaysResponseDTO>> response =
                offDaysController.getAllOffDays(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /list/{offDaysId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return off day details by ID with status 200")
    void getOffDaysById_shouldReturnDetails() {
        OffDaysAllDetailsDTO dto = new OffDaysAllDetailsDTO();

        when(offDaysService.getOffDaysById(offDaysId)).thenReturn(dto);

        ResponseEntity<OffDaysAllDetailsDTO> response =
                offDaysController.getOffDaysById(offDaysId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(offDaysService).getOffDaysById(offDaysId);
    }

    // -------------------------------------------------------------------------
    // DELETE /delete/{offDaysId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete off day and return 204 No Content")
    void deleteOffDays_shouldReturn204() {
        doNothing().when(offDaysService).deleteOffDaysById(offDaysId);

        ResponseEntity<Void> response =
                offDaysController.deleteOffDays(offDaysId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(offDaysService).deleteOffDaysById(offDaysId);
    }

    @Test
    @DisplayName("Should call deleteOffDaysById exactly once with correct ID")
    void deleteOffDays_shouldInvokeServiceOnce() {
        doNothing().when(offDaysService).deleteOffDaysById(any());

        offDaysController.deleteOffDays(offDaysId);

        verify(offDaysService, times(1)).deleteOffDaysById(offDaysId);
        verifyNoMoreInteractions(offDaysService);
    }

    // -------------------------------------------------------------------------
    // PUT /update/{offDaysId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update off day and return 200 with updated body")
    void updateOffDays_shouldReturn200WithBody() {
        OffDayUpdateDTO requestDTO = new OffDayUpdateDTO();
        OffDayUpdateDTO responseDTO = new OffDayUpdateDTO();

        when(offDaysService.updateService(eq(offDaysId), any(OffDayUpdateDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<OffDayUpdateDTO> response =
                offDaysController.updateOffDays(offDaysId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(offDaysService).updateService(offDaysId, requestDTO);
    }

    @Test
    @DisplayName("Should call updateService exactly once with correct arguments")
    void updateOffDays_shouldInvokeServiceOnce() {
        OffDayUpdateDTO requestDTO = new OffDayUpdateDTO();

        when(offDaysService.updateService(any(), any(OffDayUpdateDTO.class)))
                .thenReturn(new OffDayUpdateDTO());

        offDaysController.updateOffDays(offDaysId, requestDTO);

        verify(offDaysService, times(1)).updateService(offDaysId, requestDTO);
        verifyNoMoreInteractions(offDaysService);
    }
}
