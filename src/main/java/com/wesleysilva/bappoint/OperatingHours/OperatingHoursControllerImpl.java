package com.wesleysilva.bappoint.OperatingHours;

import com.wesleysilva.bappoint.OperatingHours.doc.OperatingHoursController;
import com.wesleysilva.bappoint.OperatingHours.dto.CreateOperatingHoursDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursAllDetailsDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.OperatingHoursResponseDTO;
import com.wesleysilva.bappoint.OperatingHours.dto.UpdateOperatingHoursDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/settings/operating_hours")
public class OperatingHoursControllerImpl implements OperatingHoursController {
    private final OperatingHoursService operatingHoursService;

    public OperatingHoursControllerImpl(OperatingHoursService operatingHoursService) {
        this.operatingHoursService = operatingHoursService;
    }


    @PostMapping("/create")
    public ResponseEntity<CreateOperatingHoursDTO> createOperatingHours(@PathVariable UUID companyId,
                                                                        @RequestBody CreateOperatingHoursDTO operatingHoursDTO) {

        CreateOperatingHoursDTO newOperatingHours = operatingHoursService.createOperatingHours(companyId, operatingHoursDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newOperatingHours);
    }


    @GetMapping("/list")
    public ResponseEntity<List<OperatingHoursResponseDTO>> listOperatingHours(){
        List<OperatingHoursResponseDTO> operatingHours = operatingHoursService.getAllOperatingHours();
        return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
    }



    @GetMapping("/{operatingHoursId}")
    public ResponseEntity<OperatingHoursAllDetailsDTO> getOperatingHoursById(@PathVariable UUID operatingHoursId) {
        OperatingHoursAllDetailsDTO operatingHours = operatingHoursService.getOperatingHoursById(operatingHoursId);
            return ResponseEntity.status(HttpStatus.OK).body(operatingHours);
    }



    @DeleteMapping("/{operatingHoursId}")
    public void deleteOperatingHoursById(@PathVariable UUID operatingHoursId) {
        operatingHoursService.deleteOperatingHoursById(operatingHoursId);
    }



    @PutMapping("/update/{operatingHoursId}")
    public ResponseEntity<UpdateOperatingHoursDTO> updateOperatingHours(@PathVariable UUID operatingHoursId,
                                                                        @RequestBody UpdateOperatingHoursDTO operatingHoursDTO){

        UpdateOperatingHoursDTO updateOperatingHours = operatingHoursService.updateService(operatingHoursId, operatingHoursDTO);
            return ResponseEntity.status(HttpStatus.OK).body(updateOperatingHours);
    }
}