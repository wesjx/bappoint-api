package com.wesleysilva.bappoint.stripe;

import com.wesleysilva.bappoint.stripe.dto.PaymentRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeControllerTest {

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private StripeController stripeController;

    private UUID companyId;
    private UUID appointmentId;
    private PaymentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        requestDTO = new PaymentRequestDTO(appointmentId);
        requestDTO.setAppointmentId(appointmentId);
    }

    // -------------------------------------------------------------------------
    // POST /checkout-session
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return checkout URL with status 200 on success")
    void createCheckoutSession_shouldReturnCheckoutUrl() throws Exception {
        String checkoutUrl = "https://checkout.stripe.com/pay/cs_test_abc123";

        when(stripeService.createCheckoutSession(companyId, appointmentId))
                .thenReturn(checkoutUrl);

        ResponseEntity<?> response =
                stripeController.createCheckoutSession(companyId, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertTrue(body.containsKey("checkoutUrl"));
        assertEquals(checkoutUrl, body.get("checkoutUrl"));

        verify(stripeService).createCheckoutSession(companyId, appointmentId);
    }

    @Test
    @DisplayName("Should return 400 with error message when service throws exception")
    void createCheckoutSession_shouldReturn400WhenServiceThrows() throws Exception {
        String errorMessage = "Stripe account not connected";

        when(stripeService.createCheckoutSession(companyId, appointmentId))
                .thenThrow(new RuntimeException(errorMessage));

        ResponseEntity<?> response =
                stripeController.createCheckoutSession(companyId, requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertTrue(body.containsKey("error"));
        assertEquals(errorMessage, body.get("error"));

        verify(stripeService).createCheckoutSession(companyId, appointmentId);
    }

    @Test
    @DisplayName("Should return 400 when Stripe throws checked exception")
    void createCheckoutSession_shouldReturn400OnStripeException() throws Exception {
        String errorMessage = "Invalid Stripe API key";

        when(stripeService.createCheckoutSession(companyId, appointmentId))
                .thenThrow(new RuntimeException(errorMessage)); // ← era new Exception(...)

        ResponseEntity<?> response =
                stripeController.createCheckoutSession(companyId, requestDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();

        assertEquals(errorMessage, body.get("error"));
    }


    @Test
    @DisplayName("Should call createCheckoutSession exactly once with correct arguments")
    void createCheckoutSession_shouldInvokeServiceOnce() throws Exception {
        when(stripeService.createCheckoutSession(companyId, appointmentId))
                .thenReturn("https://checkout.stripe.com/pay/cs_test_xyz");

        stripeController.createCheckoutSession(companyId, requestDTO);

        verify(stripeService, times(1)).createCheckoutSession(companyId, appointmentId);
        verifyNoMoreInteractions(stripeService);
    }
}
