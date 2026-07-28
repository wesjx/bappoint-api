package com.wesleysilva.bappoint.clerk;

import com.wesleysilva.bappoint.ratelimit.PublicEndpointRateLimitFilter;
import com.wesleysilva.bappoint.ratelimit.RateLimiterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final ClerkJwtFilter clerkJwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final RateLimiterService rateLimiterService;

    public SecurityConfig(ClerkJwtFilter clerkJwtFilter,
                          CorsConfigurationSource corsConfigurationSource,
                          RateLimiterService rateLimiterService) {
        this.clerkJwtFilter = clerkJwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
        this.rateLimiterService = rateLimiterService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/error",
                                "/companies/slug/**",
                                "/companies/*/appointments/available-times",
                                "/companies/*/appointments/create",
                                "/appointments/create",
                                "/api/webhooks/stripe",
                                "/stripe/checkout-session",
                                "/companies/*/stripe/checkout-session",
                                "/stripe/webhook",
                                "/companies/*/stripe/checkout-session/*"
                        ).permitAll()
                        .requestMatchers("/companies/create").hasRole("MASTER")
                        .requestMatchers("/companies/delete/**").hasRole("MASTER")
                        .requestMatchers("/companies/list").hasRole("MASTER")
                        .anyRequest().hasAnyRole("MASTER", "COMPANY_ADMIN")
                )
                .addFilterBefore(
                        new PublicEndpointRateLimitFilter(rateLimiterService),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(clerkJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}