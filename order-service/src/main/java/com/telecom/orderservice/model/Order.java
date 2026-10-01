package com.telecom.orderservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(
    name = "orders",
    indexes = {
        @Index(
            name = "idx_order_customer",
            columnList = "customer_id"
        ),
        @Index(
            name = "idx_order_mobile_line",
            columnList = "mobile_line_id"
        ),
        @Index(
            name = "idx_order_subscription",
            columnList = "subscription_id"
        ),
        @Index(
            name = "idx_order_status",
            columnList = "status"
        )
    }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "customer_id",
        nullable = false
    )
    private Long customerId;

    @Column(
        name = "mobile_line_id",
        nullable = false
    )
    private Long mobileLineId;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "requested_plan_id")
    private Long requestedPlanId;

    @Column(
        nullable = false,
        updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public Order() {
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

    public Long getVersion() {
        return version;
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

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setRequestedPlanId(Long requestedPlanId) {
        this.requestedPlanId = requestedPlanId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}