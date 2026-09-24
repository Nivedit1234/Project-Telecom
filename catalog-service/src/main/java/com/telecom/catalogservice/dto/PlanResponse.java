package com.telecom.catalogservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponse {

    private Long id;

    private String code;

    private String name;

    private String description;

    private BigDecimal monthlyPrice;

    private BigDecimal callRatePerMinute;

    private BigDecimal dataRatePerMB;

    private BigDecimal smsRate;

    private Integer validityDays;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}