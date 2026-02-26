package com.wesleysilva.bappoint.Services.doc;

import com.wesleysilva.bappoint.Services.dto.CreateServiceDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceAllDetailsDTO;
import com.wesleysilva.bappoint.Services.dto.ServiceResponseDTO;
import com.wesleysilva.bappoint.Services.dto.UpdateServiceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.UUID;

@Tag(name = "dev/Services", description = "Manage company services")
public interface ServiceController {


    @Operation(
            summary = "Create service for company",
            description = "Creates a new service for a specific company. Validates service name, price, duration and active status. Service is automatically associated with company settings."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Service created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "serviceCreated",
                                            summary = "New service created",
                                            value = """
                        {
                            "id": "f5e590a0-1514-4bb6-b8cb-8313e1b38698",
                            "name": "Male hair",
                            "price": 50.00,
                            "durationMinutes": 30,
                            "isActive": true,
                            "settings": {
                                "id": "578466da-a125-4150-a235-a94d16a81736",
                                "appointmentInterval": "THIRTY_MINUTES"
                            }
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid service data - missing required fields (name, price, duration) or company settings"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Company or company settings not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service name already exists for this company"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<CreateServiceDTO> createService(@PathVariable UUID companyId,
                                                   @Valid @RequestBody CreateServiceDTO service);


    @Operation(
            summary = "List services for company",
            description = "Returns all services associated with the company from the authenticated context. Does NOT require companyId parameter - uses company from JWT/security context."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Services listed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "servicesList",
                                            summary = "List of company services",
                                            value = """
                        [
                            {
                                "id": "f5e590a0-1514-4bb6-b8cb-8313e1b38698",
                                "name": "Male cut",
                                "price": 50.00,
                                "durationMinutes": 30,
                                "isActive": true
                            },
                            {
                                "id": "a2b3c4d5-e6f7-8901-2345-6789abcdef01",
                                "name": "Beard full",
                                "price": 35.00,
                                "durationMinutes": 20,
                                "isActive": true
                            }
                        ]
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<List<ServiceResponseDTO>> listServices();



    @Operation(
            summary = "Get service by ID",
            description = "Retrieves complete details of a specific service by UUID, including company and settings information. Returns 404 if service not found."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "serviceDetails",
                                            summary = "Complete service details with company info",
                                            value = """
                        {
                            "id": "f5e590a0-1514-4bb6-b8cb-8313e1b38698",
                            "name": "Male hair",
                            "price": 50.00,
                            "durationMinutes": 30,
                            "isActive": true,
                            "company": {
                                "id": "6eab4b1c-b25c-41ac-b837-f4c3b0415c68",
                                "name": "Barber XPTO",
                                "email": "xpto@barba.com",
                                "phone": "8888888"
                            },
                            "settings": {
                                "id": "578466da-a125-4150-a235-a94d16a81736",
                                "appointmentInterval": "THIRTY_MINUTES",
                                "maxCancellationInterval": 24
                            }
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<ServiceAllDetailsDTO> getServiceById(@PathVariable UUID serviceId);



    @Operation(
            summary = "Delete service",
            description = "Deletes a specific service by UUID. Returns 204 No Content on success (REST standard). Only active services can be deleted."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Service deleted successfully",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<String> deleteService(@PathVariable UUID serviceId);



    @Operation(
            summary = "Update service",
            description = "Updates an existing service by UUID. Supports partial updates - only sent fields will be modified. Required: name, price, duration. Validates business rules before update."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Service updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "updatedService",
                                            summary = "Service updated with new price and duration",
                                            value = """
                        {
                            "id": "f5e590a0-1514-4bb6-b8cb-8313e1b38698",
                            "name": "Wash premium",
                            "price": 65.00,
                            "durationMinutes": 45,
                            "isActive": true
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid data - missing required fields (name, price, durationMinutes)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Service not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Service name already exists for this company"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    ResponseEntity<UpdateServiceDTO> updateService(@PathVariable UUID serviceId,
                                                          @Valid @RequestBody CreateServiceDTO service);
}
