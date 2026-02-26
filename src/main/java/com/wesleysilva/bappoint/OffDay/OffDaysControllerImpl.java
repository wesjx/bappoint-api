package com.wesleysilva.bappoint.OffDay;

import com.wesleysilva.bappoint.OffDay.doc.OffDaysController;
import com.wesleysilva.bappoint.OffDay.dto.CreateOffDayDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDayUpdateDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysAllDetailsDTO;
import com.wesleysilva.bappoint.OffDay.dto.OffDaysResponseDTO;
import com.wesleysilva.bappoint.exceptions.OffDayNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/settings/off_days")

public class OffDaysControllerImpl implements OffDaysController {
    private final OffDaysService offDaysService;

    public OffDaysControllerImpl(OffDaysService offDaysService) {
        this.offDaysService = offDaysService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateOffDayDTO> createOffDays(@Valid @PathVariable UUID companyId, @RequestBody CreateOffDayDTO offDaysDTO) {
        CreateOffDayDTO newOffDays = offDaysService.createOffDays(companyId, offDaysDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOffDays);
    }



    @GetMapping("/list")
    public ResponseEntity<List<OffDaysResponseDTO>> getAllOffDays() {
        List<OffDaysResponseDTO> offDaysDTOs = offDaysService.getAllOffDays();
        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTOs);
    }



    @GetMapping("/list/{offDaysId}")
    public ResponseEntity<OffDaysAllDetailsDTO> getOffDaysById(@PathVariable UUID offDaysId){
        OffDaysAllDetailsDTO offDaysDTO = offDaysService.getOffDaysById(offDaysId);

        return ResponseEntity.status(HttpStatus.OK).body(offDaysDTO);
    }



    @DeleteMapping("/delete/{offDaysId}")
    public void deleteOffDays(@PathVariable UUID offDaysId){

        if (offDaysService.getOffDaysById(offDaysId) != null ){
        offDaysService.deleteOffDaysById(offDaysId);
        } else {
            throw new OffDayNotFoundException();
        }
    }



    @PutMapping("/update/{offDaysId}")
    public ResponseEntity<OffDayUpdateDTO> updateOffDays(@PathVariable UUID offDaysId,
                                                         @Valid @RequestBody OffDayUpdateDTO offDaysDTO) {
        OffDayUpdateDTO updateOffDays = offDaysService.updateService(offDaysId, offDaysDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updateOffDays);
    }
}



