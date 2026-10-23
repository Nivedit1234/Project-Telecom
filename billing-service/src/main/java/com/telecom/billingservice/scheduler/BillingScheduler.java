package com.telecom.billingservice.scheduler;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.telecom.billingservice.client.CatalogServiceClient;
import com.telecom.billingservice.client.SubscriptionServiceClient;
import com.telecom.billingservice.dto.BillingRequest;
import com.telecom.billingservice.service.BillingService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BillingScheduler {

    private final SubscriptionServiceClient subscriptionServiceClient;
    private final CatalogServiceClient catalogServiceClient;
    private final BillingService billingService;

    public BillingScheduler(
            SubscriptionServiceClient subscriptionServiceClient,
            CatalogServiceClient catalogServiceClient,
            BillingService billingService) {

        this.subscriptionServiceClient = subscriptionServiceClient;
        this.catalogServiceClient = catalogServiceClient;
        this.billingService = billingService;
    }

    /*
     * Runs every hour.
     *
     * The scheduler is intentionally hourly because billing due dates
     * are date-based, and this also makes the system easy to demonstrate.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void generateDueBills() {

        log.info("Billing scheduler started");

        try {

            List<SubscriptionServiceClient.SubscriptionDetails>
                    subscriptions =
                    subscriptionServiceClient
                            .getActiveSubscriptions();

            log.info(
                    "Found {} active subscriptions",
                    subscriptions.size()
            );

            LocalDate today = LocalDate.now();

            for (SubscriptionServiceClient.SubscriptionDetails subscription
                    : subscriptions) {

                try {

                    processSubscription(subscription, today);

                } catch (Exception exception) {

                    log.error(
                            "Failed to process billing for subscriptionId={}",
                            subscription.id(),
                            exception
                    );
                }
            }

        } catch (Exception exception) {

            log.error(
                    "Billing scheduler failed",
                    exception
            );
        }

        log.info("Billing scheduler completed");
    }

    private void processSubscription(
            SubscriptionServiceClient.SubscriptionDetails subscription,
            LocalDate today) {

        CatalogServiceClient.PlanDetails plan =
                catalogServiceClient.getPlanById(
                        subscription.planId()
                );

        if (plan == null || plan.validityDays() == null
                || plan.validityDays() <= 0) {

            log.warn(
                    "Skipping subscriptionId={} because plan validityDays is invalid",
                    subscription.id()
            );

            return;
        }

        LocalDate startDate =
                subscription.startDate();

        if (startDate == null) {

            log.warn(
                    "Skipping subscriptionId={} because startDate is null",
                    subscription.id()
            );

            return;
        }

        if (subscription.endDate() != null
                && !subscription.endDate().isAfter(today)) {

            log.info(
                    "Skipping subscriptionId={} because subscription ended on {}",
                    subscription.id(),
                    subscription.endDate()
            );

            return;
        }

        long daysSinceStart =
                ChronoUnit.DAYS.between(startDate, today);

        if (daysSinceStart < 0) {

            log.info(
                    "Skipping subscriptionId={} because subscription has not started",
                    subscription.id()
            );

            return;
        }

        long cycleNumber =
                daysSinceStart / plan.validityDays();

        LocalDate billingPeriodStart =
                startDate.plusDays(
                        cycleNumber * plan.validityDays()
                );

        LocalDate billingPeriodEnd =
                billingPeriodStart.plusDays(
                        plan.validityDays() - 1
                );

        /*
         * Only generate a bill when the current billing cycle
         * has completed.
         */
        if (billingPeriodEnd.isAfter(today)) {

            log.info(
                    "SubscriptionId={} is not due yet. Billing period={} to={}",
                    subscription.id(),
                    billingPeriodStart,
                    billingPeriodEnd
            );

            return;
        }

        SubscriptionServiceClient.MobileLineDetails mobileLine =
                subscriptionServiceClient.getMobileLine(
                        subscription.mobileLineId()
                );

        if (mobileLine == null
                || mobileLine.customerId() == null) {

            log.warn(
                    "Skipping subscriptionId={} because customer information is unavailable",
                    subscription.id()
            );

            return;
        }

        BillingRequest request =
                new BillingRequest(
                        mobileLine.customerId(),
                        subscription.mobileLineId(),
                        subscription.id(),
                        subscription.planId(),
                        billingPeriodStart,
                        billingPeriodEnd
                );

        log.info(
                "Generating billing for subscriptionId={}, period={} to={}",
                subscription.id(),
                billingPeriodStart,
                billingPeriodEnd
        );

        billingService.generateBilling(request);

        log.info(
                "Billing completed for subscriptionId={}, period={} to={}",
                subscription.id(),
                billingPeriodStart,
                billingPeriodEnd
        );
    }
}