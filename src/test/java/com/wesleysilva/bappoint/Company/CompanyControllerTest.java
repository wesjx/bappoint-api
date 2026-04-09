package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
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
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private UUID companyId;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
    }

    // -------------------------------------------------------------------------
    // POST /create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create company and return 201")
    void createCompany_shouldReturn201WithBody() {
        CreateCompanyDTO requestDTO = new CreateCompanyDTO();
        CreateCompanyDTO responseDTO = new CreateCompanyDTO();

        when(companyService.createCompany(any(CreateCompanyDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<CreateCompanyDTO> response =
                companyController.createCompany(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(companyService).createCompany(requestDTO);
    }

    @Test
    @DisplayName("Should call createCompany exactly once")
    void createCompany_shouldInvokeServiceOnce() {
        CreateCompanyDTO requestDTO = new CreateCompanyDTO();

        when(companyService.createCompany(any(CreateCompanyDTO.class)))
                .thenReturn(new CreateCompanyDTO());

        companyController.createCompany(requestDTO);

        verify(companyService, times(1)).createCompany(any(CreateCompanyDTO.class));
        verifyNoMoreInteractions(companyService);
    }

    // -------------------------------------------------------------------------
    // GET /list
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of companies with status 200")
    void listCompanies_shouldReturnList() {
        List<CompanyResponseDTO> companies = List.of(
                new CompanyResponseDTO(),
                new CompanyResponseDTO()
        );

        when(companyService.listCompanies()).thenReturn(companies);

        ResponseEntity<List<CompanyResponseDTO>> response =
                companyController.listCompanies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(companyService).listCompanies();
    }

    @Test
    @DisplayName("Should return empty list when no companies exist")
    void listCompanies_shouldReturnEmptyList() {
        when(companyService.listCompanies()).thenReturn(List.of());

        ResponseEntity<List<CompanyResponseDTO>> response =
                companyController.listCompanies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    // -------------------------------------------------------------------------
    // GET /list/{companyId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return company details by ID with status 200")
    void getCompanyById_shouldReturnCompanyDetails() {
        CompanyDetailsResponseDTO dto = new CompanyDetailsResponseDTO();

        when(companyService.getCompanyById(companyId)).thenReturn(dto);

        ResponseEntity<CompanyDetailsResponseDTO> response =
                companyController.getCompanyById(companyId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(companyService).getCompanyById(companyId);
    }

    // -------------------------------------------------------------------------
    // DELETE /delete/{companyId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete company and return 204 No Content")
    void deleteCompany_shouldReturn204() {
        doNothing().when(companyService).deleteCompany(companyId);

        ResponseEntity<String> response =
                companyController.deleteCompany(companyId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(companyService).deleteCompany(companyId);
    }

    @Test
    @DisplayName("Should call deleteCompany exactly once with correct ID")
    void deleteCompany_shouldInvokeServiceOnce() {
        doNothing().when(companyService).deleteCompany(any());

        companyController.deleteCompany(companyId);

        verify(companyService, times(1)).deleteCompany(companyId);
        verifyNoMoreInteractions(companyService);
    }

    // -------------------------------------------------------------------------
    // PUT /update/{companyId}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update company and return 200 when company exists")
    void updateCompany_shouldReturn200WhenFound() {
        UpdateCompanyDTO requestDTO = new UpdateCompanyDTO();
        CompanyResponseDTO responseDTO = new CompanyResponseDTO();

        when(companyService.updateCompany(eq(companyId), any(UpdateCompanyDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<?> response =
                companyController.updateCompany(companyId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(responseDTO, response.getBody());
        verify(companyService).updateCompany(companyId, requestDTO);
    }

    @Test
    @DisplayName("Should return 404 when company is not found on update")
    void updateCompany_shouldReturn404WhenNotFound() {
        UpdateCompanyDTO requestDTO = new UpdateCompanyDTO();

        // Service returns null when company does not exist
        when(companyService.updateCompany(eq(companyId), any(UpdateCompanyDTO.class)))
                .thenReturn(null);

        ResponseEntity<?> response =
                companyController.updateCompany(companyId, requestDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Company not found.", response.getBody());
        verify(companyService).updateCompany(companyId, requestDTO);
    }

    @Test
    @DisplayName("Should call updateCompany exactly once with correct arguments")
    void updateCompany_shouldInvokeServiceOnce() {
        UpdateCompanyDTO requestDTO = new UpdateCompanyDTO();

        when(companyService.updateCompany(any(), any()))
                .thenReturn(new CompanyResponseDTO());

        companyController.updateCompany(companyId, requestDTO);

        verify(companyService, times(1)).updateCompany(companyId, requestDTO);
        verifyNoMoreInteractions(companyService);
    }
}
