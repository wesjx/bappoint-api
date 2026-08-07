package com.wesleysilva.bappoint.publicaction;

import com.wesleysilva.bappoint.enums.PublicActionType;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class PublicActionTokenService {

    private final PublicActionTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();

    public PublicActionTokenService(PublicActionTokenRepository repository) {
        this.repository = repository;
    }

    public String createStripeConnectOnboardingToken(UUID companyId) {
        String rawToken = generateRawToken();

        PublicActionTokenModel token = PublicActionTokenModel.builder()
                .companyId(companyId)
                .tokenHash(hashToken(rawToken))
                .actionType(PublicActionType.STRIPE_CONNECT_ONBOARDING)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .used(false)
                .createdAt(Instant.now())
                .build();

        repository.save(token);
        return rawToken;
    }

    public PublicActionTokenModel validateStripeConnectOnboardingToken(String rawToken) {
        String tokenHash = hashToken(rawToken);

        PublicActionTokenModel token = repository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token."));

        if (token.getActionType() != PublicActionType.STRIPE_CONNECT_ONBOARDING) {
            throw new IllegalArgumentException("Invalid token type.");
        }

        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token expired.");
        }

        return token;
    }

    public void markAsUsed(PublicActionTokenModel token) {
        token.setUsed(true);
        token.setUsedAt(Instant.now());
        repository.save(token);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing token.", e);
        }
    }
}
