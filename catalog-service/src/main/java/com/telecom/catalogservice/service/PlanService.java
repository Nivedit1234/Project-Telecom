package com.telecom.catalogservice.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.catalogservice.dto.PlanRequest;
import com.telecom.catalogservice.dto.PlanResponse;
import com.telecom.catalogservice.exception.DuplicateResourceException;
import com.telecom.catalogservice.exception.ResourceNotFoundException;
import com.telecom.catalogservice.model.Plan;
import com.telecom.catalogservice.repository.PlanRepository;

@Service
public class PlanService {

    private final PlanRepository planRepository;

    public PlanService(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Cacheable(
        value = "plans",
        key = "'active'"
    )
    @Transactional(readOnly = true)
    public List<PlanResponse> getActivePlans() {

        return planRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Cacheable(
        value = "plan",
        key = "#id"
    )
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Long id) {

        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan not found with id: " + id));

        return mapToResponse(plan);
    }

    @Cacheable(
        value = "planCode",
        key = "#code"
    )
    @Transactional(readOnly = true)
    public PlanResponse getPlanByCode(String code) {

        Plan plan = planRepository.findByCode(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan not found with code: " + code));

        return mapToResponse(plan);
    }

    @CacheEvict(
        value = {
            "plans",
            "plan",
            "planCode"
        },
        allEntries = true
    )
    @Transactional
    public PlanResponse createPlan(PlanRequest request) {

        if (planRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                    "Plan with code already exists: "
                            + request.getCode());
        }

        Plan plan = new Plan();

        mapRequestToEntity(request, plan);

        Plan savedPlan = planRepository.save(plan);

        return mapToResponse(savedPlan);
    }

    @CacheEvict(
        value = {
            "plans",
            "plan",
            "planCode"
        },
        allEntries = true
    )
    @Transactional
    public PlanResponse updatePlan(
            Long id,
            PlanRequest request) {

        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan not found with id: " + id));

        planRepository.findByCode(request.getCode())
                .filter(existingPlan ->
                        !existingPlan.getId().equals(id))
                .ifPresent(existingPlan -> {
                    throw new DuplicateResourceException(
                            "Plan with code already exists: "
                                    + request.getCode());
                });

        mapRequestToEntity(request, plan);

        Plan updatedPlan =
                planRepository.save(plan);

        return mapToResponse(updatedPlan);
    }

    @CacheEvict(
        value = {
            "plans",
            "plan",
            "planCode"
        },
        allEntries = true
    )
    @Transactional
    public void deactivatePlan(Long id) {

        Plan plan = planRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plan not found with id: " + id));

        plan.setActive(false);

        planRepository.save(plan);
    }

    private void mapRequestToEntity(
            PlanRequest request,
            Plan plan) {

        plan.setCode(request.getCode());
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setMonthlyPrice(request.getMonthlyPrice());
        plan.setCallRatePerMinute(
                request.getCallRatePerMinute());
        plan.setDataRatePerMB(
                request.getDataRatePerMB());
        plan.setSmsRate(
                request.getSmsRate());
        plan.setValidityDays(
                request.getValidityDays());
        plan.setActive(request.isActive());
    }

    private PlanResponse mapToResponse(Plan plan) {

        return new PlanResponse(
                plan.getId(),
                plan.getCode(),
                plan.getName(),
                plan.getDescription(),
                plan.getMonthlyPrice(),
                plan.getCallRatePerMinute(),
                plan.getDataRatePerMB(),
                plan.getSmsRate(),
                plan.getValidityDays(),
                plan.isActive(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}