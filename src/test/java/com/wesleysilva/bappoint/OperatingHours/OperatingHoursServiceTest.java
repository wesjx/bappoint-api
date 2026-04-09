package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.enums.WeekDay;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.ExistsWeekDayException;
import com.wesleysilva.bappoint.exceptions.OperatingHoursNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OperatingHoursServiceTest {

    @Mock private OperatingHoursRepository operatingHoursRepository;
    @Mock private OperatingHoursMapper operatingHoursMapper;
    @Mock private CompanyRepository companyRepository;

    @InjectMocks
    private OperatingHoursService operatingHoursService;

    private UUID companyId;
    private UUID operatingHoursId;
    private CompanyModel company;
    private SettingsModel settings;
    private OperatingHoursModel operatingHoursModel;
    private CreateOperatingHoursDTO createDTO;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        operatingHoursId = UUID.randomUUID();

        settings = new SettingsModel();
        settings.setId(UUID.randomUUID());

        company = new CompanyModel();
        company.setId(companyId);
        company.setSettings(settings);

        operatingHoursModel = new OperatingHoursModel();
        operatingHoursModel.setId(operatingHoursId);

        createDTO = new CreateOperatingHoursDTO();
        createDTO.setWeekday(WeekDay.MONDAY);
    }

    // -------------------------------------------------------------------------
    // createOperatingHours
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create operating hours successfully and return DTO")
    void createOperatingHours_shouldCreateSuccessfully() {
        CreateOperatingHoursDTO responseDTO = new CreateOperatingHoursDTO();

        when(operatingHoursMapper.toEntity(createDTO)).thenReturn(operatingHoursModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(operatingHoursRepository.findBySettingsAndWeekday(settings, WeekDay.MONDAY))
                .thenReturn(Optional.empty());
        when(operatingHoursRepository.save(operatingHoursModel)).thenReturn(operatingHoursModel);
        when(operatingHoursMapper.toCreate(operatingHoursModel)).thenReturn(responseDTO);

        CreateOperatingHoursDTO result = operatingHoursService.createOperatingHours(companyId, createDTO);

        assertNotNull(result);

        // Ensure settings were linked before saving
        assertEquals(settings, operatingHoursModel.getSettings());

        verify(operatingHoursRepository).save(operatingHoursModel);
        verify(operatingHoursMapper).toCreate(operatingHoursModel);
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist")
    void createOperatingHours_shouldThrowWhenCompanyNotFound() {
        when(operatingHoursMapper.toEntity(createDTO)).thenReturn(operatingHoursModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                operatingHoursService.createOperatingHours(companyId, createDTO)
        );

        verify(operatingHoursRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw SettingsNotFoundException when company has no settings")
    void createOperatingHours_shouldThrowWhenSettingsIsNull() {
        company.setSettings(null);

        when(operatingHoursMapper.toEntity(createDTO)).thenReturn(operatingHoursModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(SettingsNotFoundException.class, () ->
                operatingHoursService.createOperatingHours(companyId, createDTO)
        );

        verify(operatingHoursRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ExistsWeekDayException when weekday already registered")
    void createOperatingHours_shouldThrowWhenWeekDayAlreadyExists() {
        when(operatingHoursMapper.toEntity(createDTO)).thenReturn(operatingHoursModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(operatingHoursRepository.findBySettingsAndWeekday(settings, WeekDay.MONDAY))
                .thenReturn(Optional.of(operatingHoursModel)); // weekday already registered

        assertThrows(ExistsWeekDayException.class, () ->
                operatingHoursService.createOperatingHours(companyId, createDTO)
        );

        verify(operatingHoursRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // getAllOperatingHours
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of all operating hours for the company")
    void getAllOperatingHours_shouldReturnList() {
        OperatingHoursModel second = new OperatingHoursModel();
        OperatingHoursResponseDTO dto1 = new OperatingHoursResponseDTO();
        OperatingHoursResponseDTO dto2 = new OperatingHoursResponseDTO();

        when(operatingHoursRepository.findBySettingsCompanyId(companyId))
                .thenReturn(List.of(operatingHoursModel, second));
        when(operatingHoursMapper.toResponse(operatingHoursModel)).thenReturn(dto1);
        when(operatingHoursMapper.toResponse(second)).thenReturn(dto2);

        List<OperatingHoursResponseDTO> result = operatingHoursService.getAllOperatingHours(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(operatingHoursRepository).findBySettingsCompanyId(companyId);
        verify(operatingHoursMapper, times(2)).toResponse(any(OperatingHoursModel.class));
    }

    @Test
    @DisplayName("Should return empty list when no operating hours exist for the company")
    void getAllOperatingHours_shouldReturnEmptyList() {
        when(operatingHoursRepository.findBySettingsCompanyId(companyId)).thenReturn(List.of());

        List<OperatingHoursResponseDTO> result = operatingHoursService.getAllOperatingHours(companyId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(operatingHoursMapper);
    }

    // -------------------------------------------------------------------------
    // getOperatingHoursById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return operating hours details when found by ID")
    void getOperatingHoursById_shouldReturnDetails() {
        OperatingHoursAllDetailsDTO detailsDTO = new OperatingHoursAllDetailsDTO();

        when(operatingHoursRepository.findById(operatingHoursId))
                .thenReturn(Optional.of(operatingHoursModel));
        when(operatingHoursMapper.toResponseAllDetails(operatingHoursModel))
                .thenReturn(detailsDTO);

        OperatingHoursAllDetailsDTO result =
                operatingHoursService.getOperatingHoursById(operatingHoursId);

        assertNotNull(result);
        verify(operatingHoursRepository).findById(operatingHoursId);
        verify(operatingHoursMapper).toResponseAllDetails(operatingHoursModel);
    }

    @Test
    @DisplayName("Should throw OperatingHoursNotFoundException when not found by ID")
    void getOperatingHoursById_shouldThrowWhenNotFound() {
        when(operatingHoursRepository.findById(operatingHoursId)).thenReturn(Optional.empty());

        assertThrows(OperatingHoursNotFoundException.class, () ->
                operatingHoursService.getOperatingHoursById(operatingHoursId)
        );

        verifyNoInteractions(operatingHoursMapper);
    }

    // -------------------------------------------------------------------------
    // deleteOperatingHoursById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete operating hours successfully when it exists")
    void deleteOperatingHoursById_shouldDeleteSuccessfully() {
        when(operatingHoursRepository.findById(operatingHoursId))
                .thenReturn(Optional.of(operatingHoursModel));
        doNothing().when(operatingHoursRepository).delete(operatingHoursModel);

        assertDoesNotThrow(() ->
                operatingHoursService.deleteOperatingHoursById(operatingHoursId)
        );

        verify(operatingHoursRepository).findById(operatingHoursId);
        verify(operatingHoursRepository).delete(operatingHoursModel);
    }

    @Test
    @DisplayName("Should throw OperatingHoursNotFoundException when not found on delete")
    void deleteOperatingHoursById_shouldThrowWhenNotFound() {
        when(operatingHoursRepository.findById(operatingHoursId)).thenReturn(Optional.empty());

        assertThrows(OperatingHoursNotFoundException.class, () ->
                operatingHoursService.deleteOperatingHoursById(operatingHoursId)
        );

        verify(operatingHoursRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should throw OperatingHoursNotFoundException when repository throws on delete")
    void deleteOperatingHoursById_shouldThrowWhenRepositoryFails() {
        when(operatingHoursRepository.findById(operatingHoursId))
                .thenReturn(Optional.of(operatingHoursModel));
        doThrow(new RuntimeException("DB error"))
                .when(operatingHoursRepository).delete(operatingHoursModel);

        assertThrows(OperatingHoursNotFoundException.class, () ->
                operatingHoursService.deleteOperatingHoursById(operatingHoursId)
        );
    }

    // -------------------------------------------------------------------------
    // updateService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update operating hours fields and return updated DTO")
    void updateService_shouldUpdateSuccessfully() {
        UpdateOperatingHoursDTO updateDTO = new UpdateOperatingHoursDTO();
        updateDTO.setWeekday(WeekDay.FRIDAY);
        updateDTO.setStartTime(LocalTime.of(9, 0));
        updateDTO.setEndTime(LocalTime.of(18, 0));
        updateDTO.setIsActive(true);

        UpdateOperatingHoursDTO responseDTO = new UpdateOperatingHoursDTO();

        when(operatingHoursRepository.findById(operatingHoursId))
                .thenReturn(Optional.of(operatingHoursModel));
        when(operatingHoursRepository.save(operatingHoursModel))
                .thenReturn(operatingHoursModel);
        when(operatingHoursMapper.toUpdate(operatingHoursModel)).thenReturn(responseDTO);

        UpdateOperatingHoursDTO result =
                operatingHoursService.updateService(operatingHoursId, updateDTO);

        assertNotNull(result);

        // Verify all fields were mutated on the model before saving
        assertEquals(WeekDay.FRIDAY, operatingHoursModel.getWeekday());
        assertEquals(LocalTime.of(9, 0), operatingHoursModel.getStartTime());
        assertEquals(LocalTime.of(18, 0), operatingHoursModel.getEndTime());
        assertTrue(operatingHoursModel.getIsActive());

        verify(operatingHoursRepository).save(operatingHoursModel);
        verify(operatingHoursMapper).toUpdate(operatingHoursModel);
    }

    @Test
    @DisplayName("Should throw OperatingHoursNotFoundException when not found on update")
    void updateService_shouldThrowWhenNotFound() {
        when(operatingHoursRepository.findById(operatingHoursId)).thenReturn(Optional.empty());

        assertThrows(OperatingHoursNotFoundException.class, () ->
                operatingHoursService.updateService(operatingHoursId, new UpdateOperatingHoursDTO())
        );

        verify(operatingHoursRepository, never()).save(any());
        verifyNoInteractions(operatingHoursMapper);
    }
}
