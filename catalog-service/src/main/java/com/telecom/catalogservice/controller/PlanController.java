package com.telecom.catalogservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.telecom.catalogservice.dto.PlanRequest;
import com.telecom.catalogservice.dto.PlanResponse;
import com.telecom.catalogservice.service.PlanService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    /*
     * Customer-facing endpoint.
     * Returns only currently active plans.
     */
    @GetMapping
    public List<PlanResponse> getActivePlans() {
        return planService.getActivePlans();
    }

    @GetMapping("/{id}")
    public PlanResponse getPlanById(
            @PathVariable Long id) {

        return planService.getPlanById(id);
    }

    @GetMapping("/code/{code}")
    public PlanResponse getPlanByCode(
            @PathVariable String code) {

        return planService.getPlanByCode(code);
    }

    /*
     * Admin operation.
     * Security restriction will be added to SecurityConfig.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanResponse createPlan(
            @Valid @RequestBody PlanRequest request) {

        return planService.createPlan(request);
    }

    /*
     * Admin operation.
     */
    @PutMapping("/{id}")
    public PlanResponse updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody PlanRequest request) {

        return planService.updatePlan(id, request);
    }

    /*
     * We deactivate instead of physically deleting a plan.
     * This is important because historical subscriptions/orders
     * may reference a plan.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivatePlan(
            @PathVariable Long id) {

        planService.deactivatePlan(id);
    }
}