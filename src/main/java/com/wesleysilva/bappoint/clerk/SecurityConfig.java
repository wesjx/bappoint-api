package com.wesleysilva.bappoint.clerk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ClerkJwtFilter clerkJwtFilter;

    public SecurityConfig(ClerkJwtFilter clerkJwtFilter) {
        this.clerkJwtFilter = clerkJwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // public routes - they do not need token
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/appointments/create"
                        ).permitAll()
                        // MASTER
                        .requestMatchers("/companies/create").hasRole("MASTER")
                        .requestMatchers("/companies/delete/**").hasRole("MASTER")
                        .requestMatchers("/companies/list").hasRole("MASTER")
                        // everything else needs auth
                        .anyRequest().hasAnyRole("MASTER", "COMPANY_ADMIN")
                )
                .addFilterBefore(clerkJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}