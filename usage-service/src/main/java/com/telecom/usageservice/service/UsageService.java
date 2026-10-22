package com.telecom.usageservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.usageservice.dto.UsageEvent;
import com.telecom.usageservice.model.DailyUsage;
import com.telecom.usageservice.model.UsageType;
import com.telecom.usageservice.repository.DailyUsageRepository;

@Service
public class UsageService {

    private final DailyUsageRepository dailyUsageRepository;

    public UsageService(DailyUsageRepository dailyUsageRepository) {
        this.dailyUsageRepository = dailyUsageRepository;
    }

    @Transactional
    public DailyUsage processUsageEvent(UsageEvent event) {

        LocalDate usageDate = event.eventTime().toLocalDate();

        DailyUsage dailyUsage = dailyUsageRepository
                .findByMobileLineIdAndUsageDate(
                        event.mobileLineId(),
                        usageDate
                )
                .orElseGet(() -> createDailyUsage(event, usageDate));

        applyUsage(dailyUsage, event);

        return dailyUsageRepository.save(dailyUsage);
    }

    private DailyUsage createDailyUsage(
            UsageEvent event,
            LocalDate usageDate) {

        DailyUsage dailyUsage = new DailyUsage();

        dailyUsage.setCustomerId(event.customerId());
        dailyUsage.setMobileLineId(event.mobileLineId());
        dailyUsage.setSubscriptionId(event.subscriptionId());
        dailyUsage.setPlanId(event.planId());
        dailyUsage.setUsageDate(usageDate);

        dailyUsage.setCallMinutes(BigDecimal.ZERO);
        dailyUsage.setDataMb(BigDecimal.ZERO);
        dailyUsage.setSmsCount(0L);

        return dailyUsage;
    }

    private void applyUsage(
            DailyUsage dailyUsage,
            UsageEvent event) {

        if (event.usageType() == UsageType.CALL) {

            dailyUsage.setCallMinutes(
                    dailyUsage.getCallMinutes()
                            .add(event.quantity())
            );

        } else if (event.usageType() == UsageType.DATA) {

            dailyUsage.setDataMb(
                    dailyUsage.getDataMb()
                            .add(event.quantity())
            );

        } else if (event.usageType() == UsageType.SMS) {

            dailyUsage.setSmsCount(
                    dailyUsage.getSmsCount()
                            + event.quantity().longValue()
            );
        }
    }
}