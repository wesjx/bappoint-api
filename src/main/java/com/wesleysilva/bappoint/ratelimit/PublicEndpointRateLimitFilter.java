package com.wesleysilva.bappoint.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class PublicEndpointRateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;

    public PublicEndpointRateLimitFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (isRateLimitedEndpoint(path, method)) {
            String ip = extractClientIp(request);

            boolean allowed = rateLimiterService.tryConsume(ip);
            if (!allowed) {
                writeTooManyRequests(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimitedEndpoint(String path, String method) {
        //Adjust if change mappings later
        boolean isGet = "GET".equalsIgnoreCase(method);

        // /companies/slug/{slug}
        if (isGet && path.startsWith("/companies/slug/")) {
            return true;
        }

        // /companies/{companyId}/appointments/available-times
        if (isGet
                && path.startsWith("/companies/")
                && path.contains("/appointments/available-times")) {
            return true;
        }

        return false;
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            // get the first ip of the list
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");

        String body = """
                {
                  "status": 429,
                  "error": "Too Many Requests",
                  "message": "Rate limit exceeded for endpoints",
                  "path": "/"
                }
                """;

        response.getWriter().write(body);
    }
}
