package com.wesleysilva.bappoint.clerk;

import com.wesleysilva.bappoint.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ClerkAuthContext {

    public String getCurrentClerkUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return (String) auth.getPrincipal();
    }

    public UserRole getCurrentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> UserRole.valueOf(a.getAuthority().replace("ROLE_", "")))
                .orElse(UserRole.COMPANY_ADMIN);
    }

    public boolean isMaster() {
        return getCurrentRole() == UserRole.MASTER;
    }
}