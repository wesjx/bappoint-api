package com.wesleysilva.bappoint.Company.doc;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

@Tag(name = "Company", description = "Endpoints for managing company details (create, list, update, and delete).")
public interface CompanyController {


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
    ResponseEntity<CreateCompanyDTO> createCompany(@Valid @RequestBody CreateCompanyDTO company);



    @Operation(
            summary = "List all companies",
            description = "Retrieves a list of all registered companies.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List successfully retrieved",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = CompanyResponseDTO.class)))
            }
    )
    ResponseEntity<List<CompanyResponseDTO>> listCompanies();



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
    ResponseEntity<CompanyDetailsResponseDTO> getCompanyById(@PathVariable UUID companyId);



    @Operation(
            summary = "Delete a company",
            description = "Permanently removes a company from the system by UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Company successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "Company not found")
            }
    )
    ResponseEntity<String> deleteCompany(@PathVariable UUID companyId);



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
    ResponseEntity<?> updateCompany(@PathVariable UUID companyId,
                                    @Valid @RequestBody UpdateCompanyDTO companyDTO);


}
