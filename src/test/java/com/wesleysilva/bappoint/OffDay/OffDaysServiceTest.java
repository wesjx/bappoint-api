package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.exceptions.CompanyNotFoundException;
import com.wesleysilva.bappoint.exceptions.OffDayNotFoundException;
import com.wesleysilva.bappoint.exceptions.SettingsNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OffDaysServiceTest {

    @Mock private OffDaysRepository offDaysRepository;
    @Mock private OffDaysMapper offDaysMapper;
    @Mock private CompanyRepository companyRepository;

    @InjectMocks
    private OffDaysService offDaysService;

    private UUID companyId;
    private UUID offDaysId;
    private CompanyModel company;
    private SettingsModel settings;
    private OffDaysModel offDaysModel;
    private CreateOffDayDTO createDTO;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        offDaysId = UUID.randomUUID();

        settings = new SettingsModel();
        settings.setId(UUID.randomUUID());

        company = new CompanyModel();
        company.setId(companyId);
        company.setSettings(settings);

        offDaysModel = new OffDaysModel();
        offDaysModel.setId(offDaysId);

        createDTO = new CreateOffDayDTO();
    }

    // -------------------------------------------------------------------------
    // createOffDays
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create off day successfully and return DTO")
    void createOffDays_shouldCreateSuccessfully() {
        CreateOffDayDTO responseDTO = new CreateOffDayDTO();

        when(offDaysMapper.toEntity(createDTO)).thenReturn(offDaysModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(offDaysRepository.save(offDaysModel)).thenReturn(offDaysModel);
        when(offDaysMapper.toCreate(offDaysModel)).thenReturn(responseDTO);

        CreateOffDayDTO result = offDaysService.createOffDays(companyId, createDTO);

        assertNotNull(result);
        verify(offDaysRepository).save(offDaysModel);
        verify(offDaysMapper).toCreate(offDaysModel);

        // Ensure settings were linked to the off day before saving
        assertEquals(settings, offDaysModel.getSettings());
    }

    @Test
    @DisplayName("Should throw CompanyNotFoundException when company does not exist")
    void createOffDays_shouldThrowWhenCompanyNotFound() {
        when(offDaysMapper.toEntity(createDTO)).thenReturn(offDaysModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () ->
                offDaysService.createOffDays(companyId, createDTO)
        );

        verify(offDaysRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw SettingsNotFoundException when company has no settings")
    void createOffDays_shouldThrowWhenSettingsIsNull() {
        company.setSettings(null);

        when(offDaysMapper.toEntity(createDTO)).thenReturn(offDaysModel);
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        assertThrows(SettingsNotFoundException.class, () ->
                offDaysService.createOffDays(companyId, createDTO)
        );

        verify(offDaysRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // getAllOffDays
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return list of all off days for the company")
    void getAllOffDays_shouldReturnList() {
        OffDaysModel second = new OffDaysModel();
        OffDaysResponseDTO dto1 = new OffDaysResponseDTO();
        OffDaysResponseDTO dto2 = new OffDaysResponseDTO();

        when(offDaysRepository.findBySettingsCompanyId(companyId))
                .thenReturn(List.of(offDaysModel, second));
        when(offDaysMapper.toResponse(offDaysModel)).thenReturn(dto1);
        when(offDaysMapper.toResponse(second)).thenReturn(dto2);

        List<OffDaysResponseDTO> result = offDaysService.getAllOffDays(companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(offDaysRepository).findBySettingsCompanyId(companyId);
        verify(offDaysMapper, times(2)).toResponse(any(OffDaysModel.class));
    }

    @Test
    @DisplayName("Should return empty list when no off days exist for the company")
    void getAllOffDays_shouldReturnEmptyList() {
        when(offDaysRepository.findBySettingsCompanyId(companyId)).thenReturn(List.of());

        List<OffDaysResponseDTO> result = offDaysService.getAllOffDays(companyId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(offDaysMapper);
    }

    // -------------------------------------------------------------------------
    // getOffDaysById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return off day details when found by ID")
    void getOffDaysById_shouldReturnDetails() {
        OffDaysAllDetailsDTO detailsDTO = new OffDaysAllDetailsDTO();

        when(offDaysRepository.findById(offDaysId)).thenReturn(Optional.of(offDaysModel));
        when(offDaysMapper.toResponseAllDetails(offDaysModel)).thenReturn(detailsDTO);

        OffDaysAllDetailsDTO result = offDaysService.getOffDaysById(offDaysId);

        assertNotNull(result);
        verify(offDaysRepository).findById(offDaysId);
        verify(offDaysMapper).toResponseAllDetails(offDaysModel);
    }

    @Test
    @DisplayName("Should throw OffDayNotFoundException when off day does not exist")
    void getOffDaysById_shouldThrowWhenNotFound() {
        when(offDaysRepository.findById(offDaysId)).thenReturn(Optional.empty());

        assertThrows(OffDayNotFoundException.class, () ->
                offDaysService.getOffDaysById(offDaysId)
        );

        verifyNoInteractions(offDaysMapper);
    }

    // -------------------------------------------------------------------------
    // deleteOffDaysById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete off day successfully when it exists")
    void deleteOffDaysById_shouldDeleteSuccessfully() {
        when(offDaysRepository.findById(offDaysId)).thenReturn(Optional.of(offDaysModel));
        doNothing().when(offDaysRepository).delete(offDaysModel);

        assertDoesNotThrow(() -> offDaysService.deleteOffDaysById(offDaysId));

        verify(offDaysRepository).findById(offDaysId);
        verify(offDaysRepository).delete(offDaysModel);
    }

    @Test
    @DisplayName("Should throw OffDayNotFoundException when off day does not exist on delete")
    void deleteOffDaysById_shouldThrowWhenNotFound() {
        when(offDaysRepository.findById(offDaysId)).thenReturn(Optional.empty());

        assertThrows(OffDayNotFoundException.class, () ->
                offDaysService.deleteOffDaysById(offDaysId)
        );

        verify(offDaysRepository, never()).delete(any());
    }

    // -------------------------------------------------------------------------
    // updateService
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update off day fields and return updated DTO")
    void updateService_shouldUpdateSuccessfully() {
        OffDayUpdateDTO updateDTO = new OffDayUpdateDTO();
        updateDTO.setReason("National Holiday");
        updateDTO.setDate(LocalDate.of(2026, 12, 25));

        OffDayUpdateDTO responseDTO = new OffDayUpdateDTO();

        when(offDaysRepository.findById(offDaysId)).thenReturn(Optional.of(offDaysModel));
        when(offDaysRepository.save(offDaysModel)).thenReturn(offDaysModel);
        when(offDaysMapper.toUpdate(offDaysModel)).thenReturn(responseDTO);

        OffDayUpdateDTO result = offDaysService.updateService(offDaysId, updateDTO);

        assertNotNull(result);

        // Verify fields were updated on the model before saving
        assertEquals("National Holiday", offDaysModel.getReason());
        assertEquals(LocalDate.of(2026, 12, 25), offDaysModel.getDate());

        verify(offDaysRepository).save(offDaysModel);
        verify(offDaysMapper).toUpdate(offDaysModel);
    }

    @Test
    @DisplayName("Should throw OffDayNotFoundException when off day does not exist on update")
    void updateService_shouldThrowWhenNotFound() {
        when(offDaysRepository.findById(offDaysId)).thenReturn(Optional.empty());

        assertThrows(OffDayNotFoundException.class, () ->
                offDaysService.updateService(offDaysId, new OffDayUpdateDTO())
        );

        verify(offDaysRepository, never()).save(any());
        verifyNoInteractions(offDaysMapper);
    }
}
