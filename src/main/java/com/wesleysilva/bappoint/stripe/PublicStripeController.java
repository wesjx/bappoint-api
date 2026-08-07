package com.wesleysilva.bappoint.stripe;

import com.wesleysilva.bappoint.publicaction.PublicActionTokenModel;
import com.wesleysilva.bappoint.publicaction.PublicActionTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/public/stripe/connect")
public class PublicStripeController {

    private final PublicActionTokenService publicActionTokenService;
    private final StripeService stripeService;

    public PublicStripeController(PublicActionTokenService publicActionTokenService,
                                  StripeService stripeService) {
        this.publicActionTokenService = publicActionTokenService;
        this.stripeService = stripeService;
    }

    @GetMapping("/onboarding")
    public ResponseEntity<Void> startOnboarding(@RequestParam String token) {
        PublicActionTokenModel publicToken =
                publicActionTokenService.validateStripeConnectOnboardingToken(token);

        String stripeUrl = stripeService.createConnectLinkFromPublicToken(
                publicToken.getCompanyId(),
                token
        );

        return ResponseEntity.status(302)
                .location(URI.create(stripeUrl))
                .build();
    }

    @GetMapping("/onboarding/refresh")
    public ResponseEntity<Void> refreshOnboarding(@RequestParam String token) {
        PublicActionTokenModel publicToken =
                publicActionTokenService.validateStripeConnectOnboardingToken(token);

        String stripeUrl = stripeService.createConnectLinkFromPublicToken(
                publicToken.getCompanyId(),
                token
        );

        return ResponseEntity.status(302)
                .location(URI.create(stripeUrl))
                .build();
    }

    @GetMapping("/onboarding/complete")
    public ResponseEntity<Void> completeOnboarding(@RequestParam String token) {
        PublicActionTokenModel publicToken =
                publicActionTokenService.validateStripeConnectOnboardingToken(token);

        stripeService.syncConnectStatus(publicToken.getCompanyId());

        return ResponseEntity.status(302)
                .location(URI.create("https://admin.bappoint.com/sign-in"))
                .build();
    }
}
