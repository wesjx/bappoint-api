package com.wesleysilva.bappoint.clerk;

import com.wesleysilva.bappoint.Company.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("clerkSecurityService")
public class ClerkSecurityService {
    private final CompanyRepository companyRepository;
    private final ClerkAuthContext clerkAuthContext;

    public ClerkSecurityService(CompanyRepository companyRepository, ClerkAuthContext clerkAuthContext) {
        this.companyRepository = companyRepository;
        this.clerkAuthContext = clerkAuthContext;
    }

    public boolean isCompanyOwner(UUID companyId) {
        if (clerkAuthContext.isMaster()) return true;
        return companyRepository.findById(companyId)
                .map(c -> c.getClerkUserId()
                        .equals(clerkAuthContext.getCurrentClerkUserId()))
                .orElse(false);
    }
}