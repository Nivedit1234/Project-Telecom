package com.telecom.catalogservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.catalogservice.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findByCode(String code);

    boolean existsByCode(String code);

    List<Plan> findByActiveTrue();
}