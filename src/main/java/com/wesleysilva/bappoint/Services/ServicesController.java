package com.wesleysilva.bappoint.Services;

import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Settings.SettingsModel;
import com.wesleysilva.bappoint.Settings.SettingsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("company/{companyId}/services")
@Tag(name = "dev/Services", description = "Manage company services")
public class ServicesController {

    private final ServiceService serviceService;
    private final CompanyRepository companyRepository;

    public ServicesController(ServiceService serviceService,
                              CompanyRepository companyRepository,
                              SettingsRepository settingsRepository) {
        this.serviceService = serviceService;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/create")
    @Operation(summary = "Create service for company", description = "")
    public ResponseEntity<ServiceDTO> createService(
            @PathVariable UUID companyId,
            @RequestBody ServiceDTO service) {

        SettingsModel settings = companyRepository.findById(companyId)
                .map(CompanyModel::getSettings)
                .orElseThrow(() -> new RuntimeException("Company or settings not found"));

        ServiceDTO newService = serviceService.createService(settings.getId(), service);
        return ResponseEntity.status(HttpStatus.CREATED).body(newService);
    }

    @GetMapping("/list")
    @Operation(summary = "List services for company", description = "")
    public ResponseEntity<List<ServiceDTO>> listServices() {
        List<ServiceDTO> serviceDTOS = serviceService.listAllServices();
        return ResponseEntity.ok(serviceDTOS);
    }

    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service by ID", description = "")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable UUID serviceId) {
        ServiceDTO service = serviceService.getServiceById(serviceId);
        if (service != null) {
            return ResponseEntity.status(HttpStatus.OK).body(service);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete/{serviceId}")
    @Operation(summary = "Delete service", description = "")
    public ResponseEntity<String> deleteService(@PathVariable UUID serviceId) {
        ServiceDTO service = serviceService.getServiceById(serviceId);
        if (service != null) {
            serviceService.deleteService(serviceId);
            return ResponseEntity.ok("Service id: " + serviceId + " deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service not found");
        }
    }

    @PutMapping("/update/{serviceId}")
    @Operation(summary = "Update service", description = "")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable UUID serviceId,
            @RequestBody ServiceDTO service) {

        ServiceDTO updatedService = serviceService.updateService(serviceId, service);
        if (updatedService != null) {
            return ResponseEntity.status(HttpStatus.OK).body(updatedService);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
