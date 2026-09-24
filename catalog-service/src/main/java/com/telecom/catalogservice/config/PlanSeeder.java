package com.telecom.catalogservice.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.catalogservice.model.Plan;
import com.telecom.catalogservice.repository.PlanRepository;

@Component
public class PlanSeeder implements CommandLineRunner {

    private final PlanRepository planRepository;

    public PlanSeeder(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        seedPlan(
                "POSTPAID100",
                "Postpaid 100",
                "Basic postpaid plan",
                "100.00",
                "0.50",
                "0.20",
                "10.00",
                30
        );

        seedPlan(
                "POSTPAID200",
                "Postpaid 200",
                "Standard postpaid plan",
                "200.00",
                "0.30",
                "0.15",
                "8.00",
                30
        );

        seedPlan(
                "UNLIMITED399",
                "Unlimited 399",
                "Unlimited calls and SMS plan",
                "399.00",
                "0.00",
                "0.00",
                "0.00",
                30
        );
    }

    private void seedPlan(
            String code,
            String name,
            String description,
            String monthlyPrice,
            String callRatePerMinute,
            String dataRatePerMB,
            String smsRate,
            int validityDays) {

        if (planRepository.existsByCode(code)) {
            return;
        }

        Plan plan = new Plan();

        plan.setCode(code);
        plan.setName(name);
        plan.setDescription(description);
        plan.setMonthlyPrice(new BigDecimal(monthlyPrice));
        plan.setCallRatePerMinute(
                new BigDecimal(callRatePerMinute)
        );
        plan.setDataRatePerMB(
                new BigDecimal(dataRatePerMB)
        );
        plan.setSmsRate(
                new BigDecimal(smsRate)
        );
        plan.setValidityDays(validityDays);
        plan.setActive(true);

        planRepository.save(plan);
    }
}