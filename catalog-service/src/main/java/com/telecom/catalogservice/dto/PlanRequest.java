package com.telecom.catalogservice.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {

    @NotBlank(message = "Plan code is required")
    @Size(max = 50, message = "Plan code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Plan name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Monthly price is required")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Monthly price cannot be negative"
    )
    private BigDecimal monthlyPrice;

    @NotNull(message = "Call rate is required")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Call rate cannot be negative"
    )
    private BigDecimal callRatePerMinute;

    @NotNull(message = "Data rate is required")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "Data rate cannot be negative"
    )
    private BigDecimal dataRatePerMB;

    @NotNull(message = "SMS rate is required")
    @DecimalMin(
        value = "0.0",
        inclusive = true,
        message = "SMS rate cannot be negative"
    )
    private BigDecimal smsRate;

    @NotNull(message = "Validity days is required")
    @Min(
        value = 1,
        message = "Validity must be at least 1 day"
    )
    private Integer validityDays;

    private boolean active = true;
}