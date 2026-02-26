package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.doc.CompanyController;
import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")

public class CompanyControllerImpl implements CompanyController {

    private final CompanyService companyService;

    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateCompanyDTO> createCompany(@Valid @RequestBody CreateCompanyDTO company) {
        CreateCompanyDTO newCompany = companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }



    @Transactional(readOnly = true)
    @GetMapping("/list")
    public ResponseEntity<List<CompanyResponseDTO>> listCompanies() {
        List<CompanyResponseDTO> companyList = companyService.listCompanies();
        return ResponseEntity.ok(companyList);
    }



    @Transactional(readOnly = true)
    @GetMapping("/list/{companyId}")
    public ResponseEntity<CompanyDetailsResponseDTO> getCompanyById(@PathVariable UUID companyId) {
        CompanyDetailsResponseDTO company = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(company);
    }



    @DeleteMapping("/delete/{companyId}")
    public ResponseEntity<String> deleteCompany(@PathVariable UUID companyId) {
        if (companyService.getCompanyById(companyId) != null) {
            companyService.deleteCompany(companyId);
            return ResponseEntity.ok("Company ID: " + companyId + " deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }



    @PutMapping("/update/{companyId}")
    public ResponseEntity<?> updateCompany(@PathVariable UUID companyId,
                                           @Valid @RequestBody UpdateCompanyDTO companyDTO) {
        CompanyResponseDTO company = companyService.updateCompany(companyId, companyDTO);
        if (company != null) {
            return ResponseEntity.ok(company);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }
}
