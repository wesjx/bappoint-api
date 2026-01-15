package com.wesleysilva.bappoint.Company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController  // ← MOVER para cima
@RequestMapping("/company")
@Tag(name = "dev/Companies", description = "Manage company details")
public class CompanyController {

    @GetMapping("/details")
    @Operation(
            summary = "Welcome message",
            description = "Returns a welcome message for the company API"
    )
    public String Details() {
        return "Details Route";
    }
}
