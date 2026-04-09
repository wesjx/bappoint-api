package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.dto.CreateSettingsDTO;
import com.wesleysilva.bappoint.enums.AppointmentInterval;
import com.wesleysilva.bappoint.exceptions.CompanyDeleteException;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyService companyService;

    private UUID companyId;
    private CompanyModel companyModel;
    private CreateCompanyDTO createDTO;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();

        companyModel = new CompanyModel();
        companyModel.setId(companyId);
        companyModel.setName("BAppoint");
        companyModel.setEmail("bappoint@email.com");

        // Settings DTO used inside createDTO
        CreateSettingsDTO settingsDTO = new CreateSettingsDTO();
        settingsDTO.setAppointmentInterval(AppointmentInterval.MINUTES_30);
        settingsDTO.setMaxCancellationInterval(1);

        createDTO = new CreateCompanyDTO();
        createDTO.setName("BAppoint");
        createDTO.setEmail("bappoint@email.com");
        createDTO.setPhone("999999999");
        createDTO.setAddress("Carlow, Ireland");
        createDTO.setSlug("bappoint");
        createDTO.setClerkUserId("clerk_123");
        createDTO.setSettings(settingsDTO);
    }

    // -------------------------------------------------------------------------
    // createCompany
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create company successfully and return DTO")
    void createCompany_shouldCreateSuccessfully() {
        when(companyRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
        when(companyRepository.save(any(CompanyModel.class))).thenReturn(companyModel);
        when(companyMapper.toCreate(companyModel)).thenReturn(createDTO);

        CreateCompanyDTO result = companyService.createCompany(createDTO);

        assertNotNull(result);
        verify(companyRepository).existsByEmail(createDTO.getEmail());
        verify(companyRepository).save(any(CompanyModel.class));
        verify(companyMapper).toCreate(companyModel);
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is already in use")
    void createCompany_shouldThrowWhenEmailAlreadyExists() {
        when(companyRepository.existsByEmail(createDTO.getEmail())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () ->
                companyService.createCompany(createDTO)
        );

        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when MaxCancellationInterval is null")
    void createCompany_shouldThrowWhenMaxCancellationIntervalIsNull() {
        createDTO.getSettings().setMaxCancellationInterval(null);

        when(companyRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                companyService.createCompany(createDTO)
        );

        verify(companyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when AppointmentInterval is null")
    void createCompany_shouldThrowWhenAppointmentIntervalIsNull() {
        createDTO.getSettings().setAppointmentInterval(null);

        when(companyRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                companyService.createCompany(createDTO)
        );

        verify(companyRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // listCompanies
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of all companies")
    void listCompanies_shouldReturnAllCompanies() {
        CompanyModel second = new CompanyModel();
        second.setId(UUID.randomUUID());

        CompanyResponseDTO dto1 = new CompanyResponseDTO();
        CompanyResponseDTO dto2 = new CompanyResponseDTO();

        when(companyRepository.findAll()).thenReturn(List.of(companyModel, second));
        when(companyMapper.toResponseDTO(companyModel)).thenReturn(dto1);
        when(companyMapper.toResponseDTO(second)).thenReturn(dto2);

        List<CompanyResponseDTO> result = companyService.listCompanies();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(companyRepository).findAll();
        verify(companyMapper, times(2)).toResponseDTO(any(CompanyModel.class));
    }

    @Test
    @DisplayName("Should return empty list when no companies exist")
    void listCompanies_shouldReturnEmptyList() {
        when(companyRepository.findAll()).thenReturn(List.of());

        List<CompanyResponseDTO> result = companyService.listCompanies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(companyMapper);
    }

    // -------------------------------------------------------------------------
    // getCompanyById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return company details when company exists")
    void getCompanyById_shouldReturnDetails() {
        CompanyDetailsResponseDTO detailsDTO = new CompanyDetailsResponseDTO();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(companyModel));
        when(companyMapper.toDetailsResponseDTO(companyModel)).thenReturn(detailsDTO);

        CompanyDetailsResponseDTO result = companyService.getCompanyById(companyId);

        assertNotNull(result);
        verify(companyRepository).findById(companyId);
        verify(companyMapper).toDetailsResponseDTO(companyModel);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist")
    void getCompanyById_shouldThrowWhenNotFound() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                companyService.getCompanyById(companyId)
        );

        verifyNoInteractions(companyMapper);
    }

    // -------------------------------------------------------------------------
    // deleteCompany
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete company successfully when it exists")
    void deleteCompany_shouldDeleteSuccessfully() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(companyModel));
        doNothing().when(companyRepository).delete(companyModel);

        assertDoesNotThrow(() -> companyService.deleteCompany(companyId));

        verify(companyRepository).findById(companyId);
        verify(companyRepository).delete(companyModel);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist on delete")
    void deleteCompany_shouldThrowWhenNotFound() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                companyService.deleteCompany(companyId)
        );

        verify(companyRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw CompanyDeleteException when repository throws on delete")
    void deleteCompany_shouldThrowCompanyDeleteExceptionOnRepositoryError() {
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(companyModel));
        doThrow(new RuntimeException("DB error")).when(companyRepository).delete(companyModel);

        assertThrows(CompanyDeleteException.class, () ->
                companyService.deleteCompany(companyId)
        );
    }

    // -------------------------------------------------------------------------
    // updateCompany
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update company and return updated DTO")
    void updateCompany_shouldUpdateSuccessfully() {
        UpdateCompanyDTO updateDTO = new UpdateCompanyDTO();
        CompanyModel updatedModel = new CompanyModel();
        updatedModel.setId(companyId);
        CompanyResponseDTO responseDTO = new CompanyResponseDTO();

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(companyModel));
        when(companyMapper.toUpdateCompany(updateDTO, companyModel)).thenReturn(updatedModel);
        when(companyRepository.save(updatedModel)).thenReturn(updatedModel);
        when(companyMapper.toResponseDTO(updatedModel)).thenReturn(responseDTO);

        CompanyResponseDTO result = companyService.updateCompany(companyId, updateDTO);

        assertNotNull(result);
        verify(companyRepository).findById(companyId);
        verify(companyMapper).toUpdateCompany(updateDTO, companyModel);
        verify(companyRepository).save(updatedModel);
        verify(companyMapper).toResponseDTO(updatedModel);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist on update")
    void updateCompany_shouldThrowWhenNotFound() {
        UpdateCompanyDTO updateDTO = new UpdateCompanyDTO();

        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                companyService.updateCompany(companyId, updateDTO)
        );

        verify(companyRepository, never()).save(any());
        verifyNoInteractions(companyMapper);
    }
}
