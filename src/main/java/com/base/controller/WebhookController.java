package com.base.controller;

import com.base.service.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final SubscriptionService subsService;

    public WebhookController(SubscriptionService subsService) {
        this.subsService = subsService;
    }

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping
    public String handleEvent(HttpServletRequest request, @RequestHeader("Stripe-Signature") String sigHeader) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }

        Event event = Webhook.constructEvent(sb.toString(), sigHeader, webhookSecret);

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session != null && session.getCustomerEmail() != null) {
                    subsService.updateOrCreate(
                            session.getCustomerEmail(),
                            session.getCustomer(),
                            session.getSubscription(),
                            "ACTIVE",
                            null
                    );
                }
            }
            case "customer.subscription.updated", "invoice.payment_succeeded" -> {
                Subscription sub = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
                if (sub != null) {
                    String email = getCustomerEmailSafe(sub.getCustomer());
                    subsService.updateOrCreate(
                            email,
                            sub.getCustomer(),
                            sub.getId(),
                            sub.getStatus(),
                            sub.getCurrentPeriodEnd()
                    );
                }
            }
            case "customer.subscription.deleted", "invoice.payment_failed" -> {
                Subscription sub = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
                if (sub != null) {
                    String email = getCustomerEmailSafe(sub.getCustomer());
                    subsService.markInactive(email);
                }
            }
        }

        return "";
    }

    /**
     * Função auxiliar para obter o email do cliente Stripe de forma segura.
     */
    private String getCustomerEmailSafe(String customerId) {
        try {
            Customer c = Customer.retrieve(customerId);
            return c.getEmail();
        } catch (StripeException e) {
            System.err.println("⚠️ Não foi possível obter email do cliente " + customerId);
            return null;
        }
    }
}