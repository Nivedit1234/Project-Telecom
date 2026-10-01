package com.telecom.orderservice.dto;

import java.time.LocalDateTime;

import com.telecom.orderservice.model.OrderStatus;
import com.telecom.orderservice.model.OrderType;

public class OrderResponse {

    private Long id;
    private Long customerId;
    private Long mobileLineId;
    private Long subscriptionId;
    private OrderType type;
    private OrderStatus status;
    private Long requestedPlanId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderResponse() {
    }

    public OrderResponse(
            Long id,
            Long customerId,
            Long mobileLineId,
            Long subscriptionId,
            OrderType type,
            OrderStatus status,
            Long requestedPlanId,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.customerId = customerId;
        this.mobileLineId = mobileLineId;
        this.subscriptionId = subscriptionId;
        this.type = type;
        this.status = status;
        this.requestedPlanId = requestedPlanId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getMobileLineId() {
        return mobileLineId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getRequestedPlanId() {
        return requestedPlanId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}