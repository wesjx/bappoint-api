package com.wesleysilva.bappoint.stripe;

import com.wesleysilva.bappoint.stripe.dto.PaymentRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/companies/{companyId}/stripe")
@Slf4j
public class StripeController {

    private final StripeService stripeService;

    public StripeController(StripeService stripeService) {
        this.stripeService = stripeService;
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
}