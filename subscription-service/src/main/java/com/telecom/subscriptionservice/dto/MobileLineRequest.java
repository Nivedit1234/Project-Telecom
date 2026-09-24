package com.telecom.subscriptionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request used when provisioning a new MobileLine.
 *
 * In our project we simulate the telecom provisioning system
 * by supplying the assigned phone number and SIM/ICCID.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileLineRequest {

    /**
     * Customer who will own the mobile line.
     */
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    /**
     * Indian-style 10 digit mobile number for this project.
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone number must contain exactly 10 digits"
    )
    private String phoneNumber;

    /**
     * SIM/ICCID.
     *
     * We use 19-20 digits to represent a realistic ICCID.
     */
    @NotBlank(message = "SIM number is required")
    @Pattern(
        regexp = "^[0-9]{19,20}$",
        message = "SIM number must contain 19 or 20 digits"
    )
    private String simNumber;
}