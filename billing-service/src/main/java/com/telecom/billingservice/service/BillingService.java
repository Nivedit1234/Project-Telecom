package com.telecom.billingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.billingservice.client.CatalogServiceClient;
import com.telecom.billingservice.client.UsageServiceClient;
import com.telecom.billingservice.dto.BillingRequest;
import com.telecom.billingservice.dto.BillingResponse;
import com.telecom.billingservice.exception.BillingNotFoundException;
import com.telecom.billingservice.exception.InvalidBillingRequestException;
import com.telecom.billingservice.model.Billing;
import com.telecom.billingservice.model.BillingStatus;
import com.telecom.billingservice.repository.BillingRepository;

@Service
public class BillingService {

    private static final int MONEY_SCALE = 2;

    private final BillingRepository billingRepository;
    private final UsageServiceClient usageServiceClient;
    private final CatalogServiceClient catalogServiceClient;

    public BillingService(
            BillingRepository billingRepository,
            UsageServiceClient usageServiceClient,
            CatalogServiceClient catalogServiceClient) {

        this.billingRepository = billingRepository;
        this.usageServiceClient = usageServiceClient;
        this.catalogServiceClient = catalogServiceClient;
    }

    @Transactional
    public BillingResponse generateBilling(BillingRequest request) {

        validateRequest(request);

        Billing existingBilling = billingRepository
                .findBySubscriptionIdAndBillingPeriodStartAndBillingPeriodEnd(
                        request.subscriptionId(),
                        request.billingPeriodStart(),
                        request.billingPeriodEnd())
                .orElse(null);

        if (existingBilling != null) {
            return toResponse(existingBilling);
        }

        List<UsageServiceClient.DailyUsageDetails> usageRecords =
                usageServiceClient.getUsageByMobileLine(
                        request.mobileLineId(),
                        request.billingPeriodStart(),
                        request.billingPeriodEnd());

        CatalogServiceClient.PlanDetails plan =
                catalogServiceClient.getPlanById(request.planId());

        if (plan == null) {
            throw new InvalidBillingRequestException(
                    "Plan not found with id: " + request.planId());
        }

        BigDecimal totalCallMinutes = usageRecords.stream()
                .map(UsageServiceClient.DailyUsageDetails::callMinutes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDataMb = usageRecords.stream()
                .map(UsageServiceClient.DailyUsageDetails::dataMb)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalSms = usageRecords.stream()
                .mapToLong(UsageServiceClient.DailyUsageDetails::smsCount)
                .sum();

        BigDecimal callCharges = totalCallMinutes
                .multiply(plan.callRatePerMinute())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal dataCharges = totalDataMb
                .multiply(plan.dataRatePerMB())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal smsCharges = BigDecimal.valueOf(totalSms)
                .multiply(plan.smsRate())
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal planCharges = plan.monthlyPrice()
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        BigDecimal totalAmount = planCharges
                .add(callCharges)
                .add(dataCharges)
                .add(smsCharges)
                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);

        Billing billing = new Billing();

        billing.setCustomerId(request.customerId());
        billing.setMobileLineId(request.mobileLineId());
        billing.setSubscriptionId(request.subscriptionId());
        billing.setPlanId(request.planId());

        billing.setBillingPeriodStart(request.billingPeriodStart());
        billing.setBillingPeriodEnd(request.billingPeriodEnd());

        billing.setCallMinutes(totalCallMinutes);
        billing.setDataMb(totalDataMb);
        billing.setSmsCount(totalSms);

        billing.setCallCharges(callCharges);
        billing.setDataCharges(dataCharges);
        billing.setSmsCharges(smsCharges);
        billing.setPlanCharges(planCharges);
        billing.setTotalAmount(totalAmount);

        billing.setStatus(BillingStatus.GENERATED);

        Billing savedBilling = billingRepository.save(billing);

        return toResponse(savedBilling);
    }

    @Transactional(readOnly = true)
    public BillingResponse getBillingById(Long id) {

        Billing billing = billingRepository.findById(id)
                .orElseThrow(() -> new BillingNotFoundException(
                        "Billing record not found with id: " + id));

        return toResponse(billing);
    }

    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingByCustomer(Long customerId) {

        return billingRepository
                .findByCustomerIdOrderByBillingPeriodStartDesc(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingByMobileLine(Long mobileLineId) {

        return billingRepository
                .findByMobileLineIdOrderByBillingPeriodStartDesc(mobileLineId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BillingResponse> getBillingBySubscription(Long subscriptionId) {

        return billingRepository
                .findBySubscriptionIdOrderByBillingPeriodStartDesc(subscriptionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateRequest(BillingRequest request) {

        if (request.billingPeriodEnd()
                .isBefore(request.billingPeriodStart())) {

            throw new InvalidBillingRequestException(
                    "Billing period end date cannot be before start date");
        }
    }

    private BillingResponse toResponse(Billing billing) {

        return new BillingResponse(
                billing.getId(),
                billing.getCustomerId(),
                billing.getMobileLineId(),
                billing.getSubscriptionId(),
                billing.getPlanId(),
                billing.getBillingPeriodStart(),
                billing.getBillingPeriodEnd(),
                billing.getCallMinutes(),
                billing.getDataMb(),
                billing.getSmsCount(),
                billing.getCallCharges(),
                billing.getDataCharges(),
                billing.getSmsCharges(),
                billing.getPlanCharges(),
                billing.getTotalAmount(),
                billing.getStatus(),
                billing.getCreatedAt(),
                billing.getUpdatedAt()
        );
    }
}