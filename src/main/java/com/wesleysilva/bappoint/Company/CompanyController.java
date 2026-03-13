package com.wesleysilva.bappoint.Company;

import com.wesleysilva.bappoint.Company.dto.CompanyDetailsResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CompanyResponseDTO;
import com.wesleysilva.bappoint.Company.dto.CreateCompanyDTO;
import com.wesleysilva.bappoint.Company.dto.UpdateCompanyDTO;
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
@Tag(name = "Company", description = "Endpoints for managing company details (create, list, update, and delete).")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(
            summary = "Create a new company",
            description = "Creates a new company record in the system and returns its details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Company successfully created",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CompanyResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid company data provided")
            }
    )
    public ResponseEntity<CreateCompanyDTO> createCompany(@Valid @RequestBody CreateCompanyDTO company) {
        CreateCompanyDTO newCompany = companyService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/list")
    @Operation(
            summary = "List all companies",
            description = "Retrieves a list of all registered companies.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List successfully retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CompanyResponseDTO.class)))
            }
    )
    public ResponseEntity<List<CompanyResponseDTO>> listCompanies() {
        List<CompanyResponseDTO> companyList = companyService.listCompanies();
        return ResponseEntity.ok(companyList);
    }

    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    @GetMapping("/list/{companyId}")
    @Operation(
            summary = "Get company by ID",
            description = "Retrieves the details of a company based on its UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Company found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CompanyResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Company not found")
            }
    )
    public ResponseEntity<CompanyDetailsResponseDTO> getCompanyById(@PathVariable UUID companyId) {
        CompanyDetailsResponseDTO company = companyService.getCompanyById(companyId);
        return ResponseEntity.ok(company);
    }

    @DeleteMapping("/delete/{companyId}")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(
            summary = "Delete a company",
            description = "Permanently removes a company from the system by UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Company successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Company not found")
            }
    )
    public ResponseEntity<String> deleteCompany(@PathVariable UUID companyId) {
        if (companyService.getCompanyById(companyId) != null) {
            companyService.deleteCompany(companyId);
            return ResponseEntity.ok("Company ID: " + companyId + " deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }

    @PutMapping("/update/{companyId}")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    @Operation(
            summary = "Update a company’s information",
            description = "Updates the information of an existing company and returns the updated entity.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Company successfully updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CompanyResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Company not found")
            }
    )
    public ResponseEntity<?> updateCompany(@PathVariable UUID companyId, @Valid @RequestBody UpdateCompanyDTO companyDTO) {
        CompanyResponseDTO company = companyService.updateCompany(companyId, companyDTO);
        if (company != null) {
            return ResponseEntity.ok(company);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
        }
    }
}
