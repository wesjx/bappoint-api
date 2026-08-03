package com.wesleysilva.bappoint.stripe;

import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final StripeService stripeService;

    public StripeWebhookController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {

        Event event;

        try {
            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    endpointSecret
            );
        } catch (Exception e) {
            log.error("Invalid Stripe signature", e);
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        try {
            if ("checkout.session.completed".equals(event.getType())) {
                stripeService.handleCheckoutCompleted(event);
            } else if ("account.updated".equals(event.getType())) {
                stripeService.handleAccountUpdated(event);
            }
        } catch (Exception e) {
            log.error("Error processing Stripe webhook event: {}", event.getType(), e);
            return ResponseEntity.internalServerError().body("Webhook processing error");
        }

        return ResponseEntity.ok("success");
    }

}
