package com.wesleysilva.bappoint.Company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController  // ← MOVER para cima
@RequestMapping("/company")
@Tag(name = "dev/Companies", description = "Manage company details")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<String> createCompany(@RequestBody CompanyDTO company) {
        CompanyDTO newCompany = companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany.toString());
    }

    @GetMapping("/list")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<List<CompanyDTO>> listCompanies() {
        List<CompanyDTO> companyModel = companyService.listCompanies();
        return ResponseEntity.status(HttpStatus.OK).body(companyModel);
    }

    @GetMapping("/list/{id}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<CompanyDTO> listCompanyById(@PathVariable UUID id) {
        CompanyDTO company = companyService.getCompanyById(id);
        if (company != null) {
            return ResponseEntity.status(HttpStatus.OK).body(company);
        } else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<String> deleteCompany(@RequestBody UUID id) {
        if(companyService.getCompanyById(id) != null) {
            companyService.deleteCompany(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("edit/{id}")
    @Operation(
            summary = "",
            description = ""
    )
    public ResponseEntity<?> editCompany(@RequestBody CompanyDTO companyDTO, @PathVariable UUID id) {
        CompanyDTO company = companyService.updateCompany(id, companyDTO);
        if (company != null) {
            return ResponseEntity.status(HttpStatus.OK).body(company);
        } else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
