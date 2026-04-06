package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceResponseDTO;
import com.wesleysilva.bappoint.Services.dto.UpdateServiceDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("companies/{companyId}/services")
public class ServicesController {

    private final ServiceService serviceService;

    public ServicesController(ServiceService serviceService) {
        this.serviceService = serviceService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<CreateServiceDTO> createService(
            @PathVariable UUID companyId,
           @Valid @RequestBody CreateServiceDTO service) {

        CreateServiceDTO newService = serviceService.createService(service, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newService);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<List<ServiceResponseDTO>> listServices(@PathVariable UUID companyId) {
        List<ServiceResponseDTO> serviceDTOS = serviceService.listAllServices(companyId);
        return ResponseEntity.ok(serviceDTOS);
    }

    @GetMapping("/{serviceId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isServiceOwner(#serviceId)")
    public ResponseEntity<ServiceAllDetailsDTO> getServiceById(@PathVariable UUID serviceId) {
        ServiceAllDetailsDTO service = serviceService.getServiceById(serviceId);
            return ResponseEntity.status(HttpStatus.OK).body(service);
    }

    @DeleteMapping("/delete/{serviceId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isServiceOwner(#serviceId)")
    public ResponseEntity<String> deleteService(@PathVariable UUID serviceId) {
            serviceService.deleteService(serviceId);
            return ResponseEntity.status(HttpStatus.OK).body("Service deleted");
    }

    @PutMapping("/update/{serviceId}")
    @PreAuthorize("hasRole('COMPANY_ADMIN') and @clerkSecurityService.isServiceOwner(#serviceId)")
    public ResponseEntity<UpdateServiceDTO> updateService(
            @PathVariable UUID serviceId,
            @Valid @RequestBody CreateServiceDTO service) {

        UpdateServiceDTO updatedService = serviceService.updateService(serviceId, service);
            return ResponseEntity.status(HttpStatus.OK).body(updatedService);
    }
}
