package com.wesleysilva.bappoint.clerk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Component
public class ClerkJwksProvider {

    @Value("${clerk.jwks.url}")
    private String jwksUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RSAPublicKey getPublicKey(String token) throws Exception {
        // Get the kid of header of token
        String[] parts = token.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
        JsonNode header = objectMapper.readTree(headerJson);
        String kid = header.get("kid").asText();

        // Search for public key in clerk
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(jwksUrl))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode jwks = objectMapper.readTree(response.body());

        // Find the correct key of kid
        for (JsonNode key : jwks.get("keys")) {
            if (kid.equals(key.get("kid").asText())) {
                BigInteger modulus = new BigInteger(1,
                        Base64.getUrlDecoder().decode(key.get("n").asText()));
                BigInteger exponent = new BigInteger(1,
                        Base64.getUrlDecoder().decode(key.get("e").asText()));

                RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
                KeyFactory factory = KeyFactory.getInstance("RSA");
                return (RSAPublicKey) factory.generatePublic(spec);
            }
        }

        throw new RuntimeException("Public key not found for kid: " + kid);
    }
}