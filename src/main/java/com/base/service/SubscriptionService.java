package com.base.service;

import com.base.entity.Subscription;
import com.base.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class SubscriptionService {
    private final SubscriptionRepository repo;

    public SubscriptionService(SubscriptionRepository repo) {
        this.repo = repo;
    }

    public Subscription updateOrCreate(String email, String customerId, String subscriptionId, String status, Long periodEnd) {
        Subscription s = repo.findByEmail(email).orElse(new Subscription());
        s.setEmail(email);
        s.setStripeCustomerId(customerId);
        s.setStripeSubscriptionId(subscriptionId);
        s.setStatus(status);
        if (periodEnd != null)
            s.setCurrentPeriodEnd(LocalDateTime.ofInstant(Instant.ofEpochSecond(periodEnd), ZoneOffset.UTC));
        return repo.save(s);
    }

    public void markInactive(String email) {
        repo.findByEmail(email).ifPresent(s -> {
            s.setStatus("INACTIVE");
            repo.save(s);
        });
    }
}