package com.wesleysilva.bappoint.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.wesleysilva.bappoint.Appointments.AppointmentModel;
import com.wesleysilva.bappoint.Appointments.AppointmentRepository;
import com.wesleysilva.bappoint.Company.CompanyModel;
import com.wesleysilva.bappoint.Company.CompanyRepository;
import com.wesleysilva.bappoint.Services.ServiceModel;
import com.wesleysilva.bappoint.enums.AppointmentStatus;
import com.wesleysilva.bappoint.exceptions.AppointmentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

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

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

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

        if(companyId == null || appointmentId == null) {
            throw new InvalidParameterException("companyId and appointmentId cannot be null");
        }

        AppointmentModel appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(AppointmentNotFoundException::new);

        if(!appointment.getCompany().getId().equals(companyId)) {
            throw new IllegalStateException("Appointment does not belong to this company");
        }

        CompanyModel company = appointment.getCompany();

        if (company.getStripeAccountId() == null) {
            throw new IllegalStateException("Company without Stripe Connect ID");
        }

        if (appointment.getAppointmentStatus() != AppointmentStatus.PENDING) {
            throw new IllegalStateException("Invalid appointment status");
        }


        //Services
        List<ServiceModel> services = appointment.getServices();

        String servicesName = services.stream()
                .map(ServiceModel::getName)
                .collect(Collectors.joining(", "));

        //Pricing
        BigDecimal totalAmount = appointment.getTotalAmount();

        BigDecimal halfAmount = totalAmount.multiply(BigDecimal.valueOf(0.5));

        long totalCents = halfAmount
                .setScale(2, RoundingMode.HALF_UP)
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

        SessionCreateParams params =
                SessionCreateParams.builder()
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

                        .setSuccessUrl(frontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                        .setCancelUrl(frontendUrl + "/cancel")

                        .putAllMetadata(Map.of(
                                "appointment_id", appointmentId.toString(),
                                "company_id", company.getId().toString()
                        ))

                        // STRIPE CONNECT
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
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
            appointment.setAppointmentStatus(AppointmentStatus.PENDING);
            appointmentRepository.save(appointment);

            log.info("Checkout created: {} for company {}", session.getId(), company.getId());

            return session.getUrl();

        } catch (StripeException e) {

            log.error("Error to create checkout.", e);
            throw new RuntimeException("Error to create checkout.");
        }
    }

    //WEBHOOK HANDLER
//    private void handleCheckoutCompleted(Event event) {
//
//        EventDataObjectDeserializer dataObjectDeserializer =
//                event.getDataObjectDeserializer();
//
//        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);
//
//        if (stripeObject instanceof Session session) {
//
//            String appointmentId = session.getMetadata().get("appointment_id");
//
//            AppointmentModel appointment =
//                    appointmentRepository.findById(UUID.fromString(appointmentId))
//                            .orElseThrow();
//
//            appointment.setAppointmentStatus(AppointmentStatus.PAID);
//            appointmentRepository.save(appointment);
//        }
//    }
}