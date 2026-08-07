package com.wesleysilva.bappoint.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.wesleysilva.bappoint.appointments.AppointmentModel;
import com.wesleysilva.bappoint.appointments.AppointmentRepository;
import com.wesleysilva.bappoint.company.CompanyModel;
import com.wesleysilva.bappoint.company.CompanyRepository;
import com.wesleysilva.bappoint.enums.PaymentSetupStatus;
import com.wesleysilva.bappoint.services.ServiceModel;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import com.wesleysilva.bappoint.exceptions.AppointmentNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.wesleysilva.bappoint.stripe.CheckoutSessionAppointmentResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StripeService {

    private final AppointmentRepository appointmentRepository;
    private final CompanyRepository companyRepository;
    private static final BigDecimal PLATFORM_FEE_PERCENT = BigDecimal.valueOf(3.1);

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${app.frontend.root-domain}")
    private String frontendUrl;

    @Value("${app.master.frontend-url}")
    private String masterFrontendUrl;

    @Value("${app.api.base-url}")
    private String apiBaseUrl;

    public StripeService(AppointmentRepository appointmentRepository,
                         CompanyRepository companyRepository) {
        this.appointmentRepository = appointmentRepository;
        this.companyRepository = companyRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createCheckoutSession(UUID companyId, UUID appointmentId) {
        if (companyId == null || appointmentId == null) {
            throw new InvalidParameterException("companyId and appointmentId cannot be null");
        }

        AppointmentModel appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);

        if (!appointment.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Appointment does not belong to this company");
        }

        CompanyModel company = appointment.getCompany();

        String companyFrontendUrl = "https://" + company.getSlug() + "." + frontendUrl;

        if (company.getStripeAccountId() == null) {
            throw new IllegalStateException("Company without Stripe Connect ID");
        }

        if (appointment.getAppointmentStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Invalid appointment status");
        }

        // Services
        List<ServiceModel> services = appointment.getServices();
        String servicesName = services.stream()
                .map(ServiceModel::getName)
                .collect(Collectors.joining(", "));

        // Pricing
        BigDecimal totalAmount = appointment.getTotalAmount();
        BigDecimal percentage = company.getDepositPercentage() != null
                ? company.getDepositPercentage().divide(BigDecimal.valueOf(100))
                : BigDecimal.valueOf(0.5);

        BigDecimal depositAmount = totalAmount.multiply(percentage);

        long totalCents = depositAmount
                .setScale(2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        BigDecimal platformFeeAmount = depositAmount
                .multiply(PLATFORM_FEE_PERCENT)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        long applicationFeeCents = platformFeeAmount
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        if (appointment.getStripeSessionId() != null) {
            try {
                Session existingSession = Session.retrieve(appointment.getStripeSessionId());
                if ("open".equals(existingSession.getStatus())) {
                    return existingSession.getUrl();
                }
            } catch (StripeException e) {
                log.warn("Old session not found, creating new one.");
            }
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.REVOLUT_PAY)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("eur")
                                                .setUnitAmount(totalCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Services: " + servicesName)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .setSuccessUrl(
                        companyFrontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}&company_id=" + company.getId()
                )
                .setCancelUrl(companyFrontendUrl + "/cancel")
                .putAllMetadata(Map.of(
                        "appointment_id", appointmentId.toString(),
                        "company_id", company.getId().toString()
                ))

                // STRIPE CONNECT
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .setApplicationFeeAmount(applicationFeeCents)
                                .setTransferData(
                                        SessionCreateParams.PaymentIntentData.TransferData.builder()
                                                .setDestination(company.getStripeAccountId())
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            Session session = Session.create(params);
            appointment.setStripeSessionId(session.getId());
            appointmentRepository.save(appointment);

            log.info("Checkout created: {} for company {}", session.getId(), company.getId());
            return session.getUrl();

        } catch (StripeException e) {
            log.error("Error creating checkout session.", e);
            throw new RuntimeException("Error creating checkout session.", e);
        }
    }

    // WEBHOOK HANDLER
    public void handleCheckoutCompleted(Event event) {
        try {

            EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

            StripeObject stripeObject;

            if (deserializer.getObject().isPresent()) {

                stripeObject = deserializer.getObject().get();

            } else {

                stripeObject = deserializer.deserializeUnsafe();

            }
            Session session = (Session) stripeObject;

            String appointmentIdStr = session.getMetadata().get("appointment_id");

            UUID appointmentId = UUID.fromString(appointmentIdStr);
            AppointmentModel appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(AppointmentNotFoundException::new);

            appointment.setAppointmentStatus(AppointmentStatus.PAID);
            appointment.setStripeSessionId(session.getId());
            appointmentRepository.save(appointment);


        } catch (Exception e) {
            log.error("Error:", e);
        }
    }

    public CheckoutSessionAppointmentResponse getAppointmentBySessionId(UUID companyId, String sessionId) {
        if (companyId == null) {
            throw new InvalidParameterException("companyId cannot be null");
        }

        if (sessionId == null || sessionId.isBlank()) {
            throw new InvalidParameterException("sessionId cannot be null or blank");
        }

        AppointmentModel appointment = appointmentRepository.findByStripeSessionId(sessionId)
                .orElseThrow(AppointmentNotFoundException::new);

        if (!appointment.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Appointment does not belong to this company");
        }

        List<String> services = appointment.getServices().stream()
                .map(ServiceModel::getName)
                .toList();

        return new CheckoutSessionAppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentStatus(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getCostumerName(),
                services,
                appointment.getCompany().getName()
        );
    }

    public String createOrReuseExpressConnectLink(UUID companyId) throws StripeException {
        if (companyId == null) {
            throw new InvalidParameterException("companyId cannot be null");
        }

        CompanyModel company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("Company not found"));

        if (company.getStripeAccountId() == null || company.getStripeAccountId().isBlank()) {
            AccountCreateParams accountParams = AccountCreateParams.builder()
                    .setType(AccountCreateParams.Type.EXPRESS)
                    .setCountry("IE")
                    .setEmail(company.getEmail())
                    .setBusinessType(AccountCreateParams.BusinessType.COMPANY)
                    .putMetadata("companyId", company.getId().toString())
                    .putMetadata("companySlug", company.getSlug())
                    .build();

            Account account = Account.create(accountParams);
            company.setStripeAccountId(account.getId());
        }

        company.setPaymentSetupStatus(PaymentSetupStatus.PENDING);
        company.setStripeConnectionError(null);
        companyRepository.save(company);

        String refreshUrl = masterFrontendUrl + "/companies/" + company.getId();
        String returnUrl = masterFrontendUrl + "/companies/" + company.getId() + "/stripe/pending";

        AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
                .setAccount(company.getStripeAccountId())
                .setRefreshUrl(refreshUrl)
                .setReturnUrl(returnUrl)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(linkParams);
        return accountLink.getUrl();
    }

    public void handleAccountUpdated(Event event) {
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        if (!(stripeObject instanceof Account account)) {
            log.warn("Stripe account.updated event without Account object. Event id={}", event.getId());
            return;
        }

        CompanyModel company = companyRepository.findByStripeAccountId(account.getId())
                .orElse(null);

        if (company == null) {
            log.warn("No company found for stripe account id={}", account.getId());
            return;
        }

        boolean chargesEnabled = Boolean.TRUE.equals(account.getChargesEnabled());
        boolean payoutsEnabled = Boolean.TRUE.equals(account.getPayoutsEnabled());

        if (chargesEnabled && payoutsEnabled) {
            company.setPaymentSetupStatus(PaymentSetupStatus.COMPLETED);

            if (company.getStripeConnectedAt() == null) {
                company.setStripeConnectedAt(java.time.Instant.now());
            }

            company.setStripeConnectionError(null);
        } else {
            company.setPaymentSetupStatus(PaymentSetupStatus.PENDING);

            if (account.getRequirements() != null
                    && account.getRequirements().getCurrentlyDue() != null
                    && !account.getRequirements().getCurrentlyDue().isEmpty()) {

                company.setStripeConnectionError(
                        "Stripe onboarding incomplete. Missing: "
                                + String.join(", ", account.getRequirements().getCurrentlyDue())
                );
            } else {
                company.setStripeConnectionError("Stripe onboarding still pending.");
            }
        }

        companyRepository.save(company);

        log.info("Stripe account sync updated for companyId={}, stripeAccountId={}, status={}",
                company.getId(),
                company.getStripeAccountId(),
                company.getPaymentSetupStatus());
    }

    public String buildPublicConnectEntryUrl(String rawToken) {
        return apiBaseUrl + "/public/stripe/connect/onboarding?token=" + rawToken;
    }

    public void syncConnectStatus(UUID companyId) {
        CompanyModel company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found."));

        syncConnectStatus(company);
    }

    public void syncConnectStatus(CompanyModel company) {
        try {
            if (company.getStripeAccountId() == null || company.getStripeAccountId().isBlank()) {
                company.setPaymentSetupStatus(PaymentSetupStatus.PENDING);
                companyRepository.save(company);
                return;
            }

            Account account = Account.retrieve(company.getStripeAccountId());

            boolean completed = Boolean.TRUE.equals(account.getDetailsSubmitted())
                    && Boolean.TRUE.equals(account.getChargesEnabled())
                    && Boolean.TRUE.equals(account.getPayoutsEnabled());

            if (completed) {
                company.setPaymentSetupStatus(PaymentSetupStatus.COMPLETED);
                company.setStripeConnectedAt(java.time.Instant.now());
                company.setStripeConnectionError(null);
            } else {
                company.setPaymentSetupStatus(PaymentSetupStatus.PENDING);
                company.setStripeConnectionError(
                        account.getRequirements() != null
                                ? String.join(", ", account.getRequirements().getCurrentlyDue())
                                : null
                );
            }

            companyRepository.save(company);
        } catch (StripeException e) {
            company.setPaymentSetupStatus(PaymentSetupStatus.ERROR);
            company.setStripeConnectionError(e.getMessage());
            companyRepository.save(company);
            throw new RuntimeException("Error syncing Stripe connect status.", e);
        }
    }

    public String createConnectLinkFromPublicToken(UUID companyId, String rawToken) {
        CompanyModel company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found."));

        try {
            if (company.getStripeAccountId() == null || company.getStripeAccountId().isBlank()) {
                AccountCreateParams accountParams = AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.EXPRESS)
                        .setCountry("IE")
                        .setEmail(company.getEmail())
                        .build();

                Account account = Account.create(accountParams);
                company.setStripeAccountId(account.getId());
                company.setPaymentSetupStatus(PaymentSetupStatus.PENDING);
                companyRepository.save(company);
            }

            AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                    .setAccount(company.getStripeAccountId())
                    .setRefreshUrl(apiBaseUrl + "/public/stripe/connect/onboarding/refresh?token=" + rawToken)
                    .setReturnUrl(apiBaseUrl + "/public/stripe/connect/onboarding/complete?token=" + rawToken)
                    .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                    .build();

            AccountLink accountLink = AccountLink.create(params);
            return accountLink.getUrl();
        } catch (StripeException e) {
            company.setPaymentSetupStatus(PaymentSetupStatus.ERROR);
            company.setStripeConnectionError(e.getMessage());
            companyRepository.save(company);
            throw new RuntimeException("Error creating Stripe connect onboarding link.", e);
        }
    }




}
