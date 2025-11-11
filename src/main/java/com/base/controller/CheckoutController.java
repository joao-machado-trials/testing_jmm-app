package com.base.controller;

/*import com.base.repository.SubscriptionRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckoutController {

    private final SubscriptionRepository repo;

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.price-id}")
    private String priceId;

    public CheckoutController(SubscriptionRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String home(@RequestParam(required = false) String email, Model model) {
        if (email != null && !email.isEmpty()) {
            var sub = repo.findByEmail(email);
            model.addAttribute("email", email);
            model.addAttribute("subscription", sub.orElse(null));
        }
        return "index";
    }

    @PostMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, Object> createCheckoutSession(@RequestParam String email) throws StripeException {
        Stripe.apiKey = secretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomerEmail(email)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPrice(priceId)
                        .build())
                .setSuccessUrl("http://localhost:8080/?email=" + email + "&success=true")
                .setCancelUrl("http://localhost:8080/?email=" + email + "&canceled=true")
                .build();

        Session session = Session.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("url", session.getUrl());
        return response;
    }
}*/

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CheckoutController {

    @Value("${stripe.secret-key}")
    private String secretKey;

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @Value("${stripe.price-id}")
    private String priceId;

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("publishableKey", publishableKey);
        return mav;
    }

    @GetMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, Object> createCheckoutSession() throws Exception {
        Stripe.apiKey = secretKey;

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                        .setSuccessUrl("http://localhost:8080/success")
                        .setCancelUrl("http://localhost:8080/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)
                                        .setPrice(priceId)
                                        .build()
                        )
                        .build();

        Session session = Session.create(params);

        Map<String, Object> response = new HashMap<>();
        response.put("id", session.getId());
        return response;
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "cancel";
    }
}