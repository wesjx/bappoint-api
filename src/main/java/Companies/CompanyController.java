package Companies;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company")
@Tag(name = "Companies", description = "Manage details of company in the system")
public class CompaniesController {

    @GetMapping("/details")
    

}
