package com.telecom.orderservice.dto;

import com.telecom.orderservice.model.OrderType;

import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Mobile line ID is required")
    private Long mobileLineId;

    private Long subscriptionId;

    @NotNull(message = "Order type is required")
    private OrderType type;

    private Long requestedPlanId;

    public OrderRequest() {
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

    public Long getRequestedPlanId() {
        return requestedPlanId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setMobileLineId(Long mobileLineId) {
        this.mobileLineId = mobileLineId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public void setRequestedPlanId(Long requestedPlanId) {
        this.requestedPlanId = requestedPlanId;
    }
}