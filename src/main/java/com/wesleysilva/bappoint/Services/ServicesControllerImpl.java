package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Services.doc.ServiceController;
import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceResponseDTO;
import com.wesleysilva.bappoint.Services.dto.UpdateServiceDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/services")
public class ServicesControllerImpl implements ServiceController {

    private final ServiceService serviceService;

    public ServicesControllerImpl(ServiceService serviceService) {
        this.serviceService = serviceService;
    }


    @PostMapping("/create")
    public ResponseEntity<CreateServiceDTO> createService(@PathVariable UUID companyId,
                                                          @Valid @RequestBody CreateServiceDTO service) {

        CreateServiceDTO newService = serviceService.createService(service, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newService);
    }


    @GetMapping("/list")
    public ResponseEntity<List<ServiceResponseDTO>> listServices() {
        List<ServiceResponseDTO> serviceDTOS = serviceService.listAllServices();
        return ResponseEntity.ok(serviceDTOS);
    }


    @GetMapping("/{serviceId}")
    public ResponseEntity<ServiceAllDetailsDTO> getServiceById(@PathVariable UUID serviceId) {
        ServiceAllDetailsDTO service = serviceService.getServiceById(serviceId);
            return ResponseEntity.status(HttpStatus.OK).body(service);
    }


    @DeleteMapping("/delete/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable UUID serviceId) {
            serviceService.deleteService(serviceId);
            return ResponseEntity.status(HttpStatus.OK).body("Service deleted");
    }


    @PutMapping("/update/{serviceId}")
    public ResponseEntity<UpdateServiceDTO> updateService(@PathVariable UUID serviceId,
                                                          @Valid @RequestBody CreateServiceDTO service) {

        UpdateServiceDTO updatedService = serviceService.updateService(serviceId, service);
            return ResponseEntity.status(HttpStatus.OK).body(updatedService);
    }
}
