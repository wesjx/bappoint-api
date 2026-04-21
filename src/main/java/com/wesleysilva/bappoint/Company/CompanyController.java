package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
import com.wesleysilva.bappoint.docs.CompanyControllerDoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/companies")
public class CompanyController implements CompanyControllerDoc {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<CreateCompanyDTO> createCompany(@Valid @RequestBody CreateCompanyDTO company) {
        CreateCompanyDTO newCompany = companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/list")
    public ResponseEntity<List<CompanyResponseDTO>> listCompanies() {
        List<CompanyResponseDTO> companyList = companyService.listCompanies();
        return ResponseEntity.ok(companyList);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    @GetMapping("/list/{companyId}")
    public ResponseEntity<CompanyDetailsResponseDTO> getCompanyById(@PathVariable UUID companyId) {
        CompanyDetailsResponseDTO company = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/delete/{companyId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<String> deleteCompany(@PathVariable UUID companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{companyId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<?> updateCompany(@PathVariable UUID companyId, @Valid @RequestBody UpdateCompanyDTO companyDTO) {
        CompanyResponseDTO company = companyService.updateCompany(companyId, companyDTO);
        if (company != null) {
            return ResponseEntity.ok(company);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }

    @GetMapping("/clerk/{clerkUserId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<CompanyDetailsResponseDTO> getByClerkUserId(@PathVariable String clerkUserId) {
        CompanyDetailsResponseDTO company = companyService.getCompanyByClerkUserId(clerkUserId);
        return ResponseEntity.ok(company);
    }
}
