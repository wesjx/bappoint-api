package com.wesleysilva.bappoint.Appointments;

import com.wesleysilva.bappoint.Appointments.dto.AppointmentAllDetailsDTO;
import com.wesleysilva.bappoint.Appointments.dto.AppointmentReponseDTO;
import com.wesleysilva.bappoint.Appointments.dto.CreateAppointmentDTO;
import com.wesleysilva.bappoint.Appointments.dto.UpdateAppointmentDTO;
import com.wesleysilva.bappoint.Availability.SlotTimesDTO;
import com.wesleysilva.bappoint.Availability.SlotsTimesService;
import com.wesleysilva.bappoint.clerk.ClerkAuthContext;
import com.wesleysilva.bappoint.clerk.ClerkSecurityService;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock private AppointmentService appointmentService;
    @Mock private SlotsTimesService slotsTimesService;
    @Mock private ClerkAuthContext clerkAuthContext;
    @Mock private ClerkSecurityService clerkSecurityService;

    @InjectMocks
    private AppointmentController appointmentController;

    private UUID companyId;
    private UUID appointmentId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // GET /available-times
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of available slots with status 200")
    void getAvailableTimes_shouldReturnSlotList() {
        List<SlotTimesDTO> slots = List.of(new SlotTimesDTO(), new SlotTimesDTO());

        when(slotsTimesService.findAvailableSlots(companyId, "2026-04-10"))
                .thenReturn(slots);

        ResponseEntity<List<SlotTimesDTO>> response =
                appointmentController.getAvailableTimes(companyId, "2026-04-10");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(slotsTimesService).findAvailableSlots(companyId, "2026-04-10");
    }

    @Test
    @DisplayName("Should return empty list when no slots are available")
    void getAvailableTimes_shouldReturnEmptyList() {
        when(slotsTimesService.findAvailableSlots(any(), anyString()))
                .thenReturn(List.of());

        ResponseEntity<List<SlotTimesDTO>> response =
                appointmentController.getAvailableTimes(companyId, "2026-04-10");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------------------------------------------------------
    // POST /create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create appointment and return 201")
    void createAppointment_shouldReturn201WithBody() {
        CreateAppointmentDTO requestDTO = new CreateAppointmentDTO();
        CreateAppointmentDTO responseDTO = new CreateAppointmentDTO();

        when(appointmentService.createAppointment(any(CreateAppointmentDTO.class), eq(companyId)))
                .thenReturn(responseDTO);

        ResponseEntity<CreateAppointmentDTO> response =
                appointmentController.createAppointment(companyId, requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(appointmentService).createAppointment(requestDTO, companyId);
    }

    // -------------------------------------------------------------------------
    // GET /list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return appointments list when user is MASTER")
    void listAppointments_asMaster_shouldReturnList() {
        List<AppointmentAllDetailsDTO> appointments = List.of(
                new AppointmentAllDetailsDTO(),
                new AppointmentAllDetailsDTO()
        );

        when(clerkAuthContext.isMaster()).thenReturn(true);
        when(appointmentService.listAppointments(companyId, 0, 10))
                .thenReturn(appointments);

        ResponseEntity<List<AppointmentAllDetailsDTO>> response =
                appointmentController.listAppointments(0, 10, companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(appointmentService).listAppointments(companyId, 0, 10);
    }

    @Test
    @DisplayName("Should return appointments list when user is company owner")
    void listAppointments_asCompanyOwner_shouldReturnList() {
        List<AppointmentAllDetailsDTO> appointments = List.of(new AppointmentAllDetailsDTO());

        when(clerkAuthContext.isMaster()).thenReturn(false);
        when(clerkSecurityService.isCompanyOwner(companyId)).thenReturn(true);
        when(appointmentService.listAppointments(companyId, 0, 10))
                .thenReturn(appointments);

        ResponseEntity<List<AppointmentAllDetailsDTO>> response =
                appointmentController.listAppointments(0, 10, companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(appointmentService).listAppointments(companyId, 0, 10);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when user is not the company owner")
    void listAppointments_notOwner_shouldThrowCompanyNotFoundException() {
        when(clerkAuthContext.isMaster()).thenReturn(false);
        when(clerkSecurityService.isCompanyOwner(companyId)).thenReturn(false);

        assertThrows(CompanyNotFoundException.class, () ->
                appointmentController.listAppointments(0, 10, companyId)
        );

        verify(appointmentService, never()).listAppointments(any(), anyInt(), anyInt());
    }

    // -------------------------------------------------------------------------
    // GET /by-date
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return appointments filtered by date with status 200")
    void listAppointmentsByDate_shouldReturnList() {
        LocalDate date = LocalDate.of(2026, 4, 10);
        List<AppointmentReponseDTO> appointments = List.of(new AppointmentReponseDTO());

        when(appointmentService.listAppointmentsByDate(date, companyId))
                .thenReturn(appointments);

        ResponseEntity<List<AppointmentReponseDTO>> response =
                appointmentController.listAppointmentsByDate(date, companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(appointmentService).listAppointmentsByDate(date, companyId);
    }

    @Test
    @DisplayName("Should return empty list when no appointments exist for given date")
    void listAppointmentsByDate_shouldReturnEmptyList() {
        LocalDate date = LocalDate.of(2026, 4, 10);

        when(appointmentService.listAppointmentsByDate(date, companyId))
                .thenReturn(List.of());

        ResponseEntity<List<AppointmentReponseDTO>> response =
                appointmentController.listAppointmentsByDate(date, companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /{appointmentId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return appointment details by ID with status 200")
    void listAppointmentById_shouldReturnAppointment() {
        AppointmentAllDetailsDTO dto = new AppointmentAllDetailsDTO();

        when(appointmentService.getAppointmentById(appointmentId))
                .thenReturn(dto);

        ResponseEntity<AppointmentAllDetailsDTO> response =
                appointmentController.listAppointmentById(appointmentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(appointmentService).getAppointmentById(appointmentId);
    }

    // -------------------------------------------------------------------------
    // PUT /update/{appointmentId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update appointment and return 200")
    void updateAppointment_shouldReturn200() {
        UpdateAppointmentDTO requestDTO = new UpdateAppointmentDTO();
        UpdateAppointmentDTO responseDTO = new UpdateAppointmentDTO();

        when(appointmentService.updateAppointment(eq(appointmentId), any(UpdateAppointmentDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<UpdateAppointmentDTO> response =
                appointmentController.updateAppointment(appointmentId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(appointmentService).updateAppointment(appointmentId, requestDTO);
    }

    // -------------------------------------------------------------------------
    // DELETE /delete/{appointmentId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete appointment and return 204 No Content")
    void deleteAppointment_shouldReturn204() {
        doNothing().when(appointmentService).deleteAppointment(appointmentId);

        ResponseEntity<Void> response =
                appointmentController.deleteAppointment(appointmentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(appointmentService).deleteAppointment(appointmentId);
    }

    @Test
    @DisplayName("Should call deleteAppointment exactly once with correct ID")
    void deleteAppointment_shouldInvokeServiceOnce() {
        doNothing().when(appointmentService).deleteAppointment(any());

        appointmentController.deleteAppointment(appointmentId);

        verify(appointmentService, times(1)).deleteAppointment(appointmentId);
        verifyNoMoreInteractions(appointmentService);
    }
}
