package com.airbnb.project.controller;

import com.airbnb.project.services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/webhook")
public class WebhookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> initiatePayment(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature") String sigHeader) {

        log.info("Payment Webhook Controller Hits..........");
        log.info("Stripe Signature Header: {}", sigHeader);
        log.info("Webhook Secret Loaded: {}", stripeWebhookSecret != null);

        try {
            Event event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    stripeWebhookSecret
            );

            log.info("Webhook signature verified!");
            log.info("Event Type: {}", event.getType());

            bookingService.capturePayment(event);

            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe webhook signature", e);
            throw new RuntimeException(e);
        }
    }
}
