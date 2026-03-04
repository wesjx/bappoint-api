package com.wesleysilva.bappoint.clerk;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class ClerkJwtFilter extends OncePerRequestFilter {

    private final ClerkJwksProvider jwksProvider;

    public ClerkJwtFilter(ClerkJwksProvider jwksProvider) {
        this.jwksProvider = jwksProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwksProvider.getPublicKey(token))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String clerkUserId = claims.getSubject();

            // extract publicMetadata of Clerk
            String role = "COMPANY_ADMIN"; // default
            Map<String, Object> publicMetadata = claims.get("public_metadata", Map.class);
            if (publicMetadata != null && publicMetadata.get("role") != null) {
                role = publicMetadata.get("role").toString();
            }

            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(clerkUserId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}