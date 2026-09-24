package com.telecom.userservice.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long id;

    private String name;

    private String email;

    private String billingAddress;

    private Long userId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}