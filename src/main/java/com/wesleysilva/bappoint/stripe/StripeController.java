package com.wesleysilva.bappoint.stripe;

import com.wesleysilva.bappoint.publicaction.PublicActionTokenService;
import com.wesleysilva.bappoint.stripe.dto.PaymentRequestDTO;
import com.wesleysilva.bappoint.stripe.dto.PublicConnectLinkResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.wesleysilva.bappoint.stripe.CheckoutSessionAppointmentResponse;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/stripe")
@Slf4j
public class StripeController {

    private final StripeService stripeService;
    private final PublicActionTokenService publicActionTokenService;

    public StripeController(StripeService stripeService,
                            PublicActionTokenService publicActionTokenService) {
        this.stripeService = stripeService;
        this.publicActionTokenService = publicActionTokenService;
    }

    @PostMapping("/checkout-session")
    public ResponseEntity<?> createCheckoutSession(
            @PathVariable UUID companyId,
            @RequestBody PaymentRequestDTO request
    ) {

        try {

            String checkoutUrl = stripeService.createCheckoutSession(
                    companyId,
                    request.getAppointmentId()
            );

            return ResponseEntity.ok(Map.of("checkoutUrl", checkoutUrl));

        } catch (Exception e) {

            log.error("Error to create checkout.", e);

            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/checkout-session/{sessionId}")
    public ResponseEntity<CheckoutSessionAppointmentResponse> getCheckoutSessionAppointment(
            @PathVariable UUID companyId,
            @PathVariable String sessionId
    ) {
        CheckoutSessionAppointmentResponse response =
                stripeService.getAppointmentBySessionId(companyId, sessionId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/connect-link")
    @PreAuthorize("hasRole('MASTER') or @clerkSecurityService.isCompanyOwner(#companyId)")
    public ResponseEntity<?> createConnectLink(@PathVariable UUID companyId) {
        try {
            String url = stripeService.createOrReuseExpressConnectLink(companyId);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (Exception e) {
            log.error("Error to create Stripe connect link.", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/connect/public-link")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<PublicConnectLinkResponseDTO> createPublicConnectLink(@PathVariable UUID companyId) {
        String rawToken = publicActionTokenService.createStripeConnectOnboardingToken(companyId);
        String url = stripeService.buildPublicConnectEntryUrl(rawToken);
        return ResponseEntity.ok(new PublicConnectLinkResponseDTO(url));
    }


}