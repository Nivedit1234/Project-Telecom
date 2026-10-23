package com.telecom.usageservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "daily_usage",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_daily_usage_mobile_line_date",
            columnNames = {"mobile_line_id", "usage_date"}
        )
    },
    indexes = {
        @Index(
            name = "idx_daily_usage_customer",
            columnList = "customer_id"
        ),
        @Index(
            name = "idx_daily_usage_mobile_line",
            columnList = "mobile_line_id"
        ),
        @Index(
            name = "idx_daily_usage_subscription",
            columnList = "subscription_id"
        ),
        @Index(
            name = "idx_daily_usage_date",
            columnList = "usage_date"
        )
    }
)
public class DailyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "mobile_line_id", nullable = false)
    private Long mobileLineId;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "plan_id", nullable = false)
    private Long planId;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "call_minutes", nullable = false, precision = 12, scale = 3)
    private BigDecimal callMinutes = BigDecimal.ZERO;

    @Column(name = "data_mb", nullable = false, precision = 15, scale = 3)
    private BigDecimal dataMb = BigDecimal.ZERO;

    @Column(name = "sms_count", nullable = false)
    private Long smsCount = 0L;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (callMinutes == null) {
            callMinutes = BigDecimal.ZERO;
        }

        if (dataMb == null) {
            dataMb = BigDecimal.ZERO;
        }

        if (smsCount == null) {
            smsCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public Long getPlanId() {
        return planId;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public BigDecimal getCallMinutes() {
        return callMinutes;
    }

    public BigDecimal getDataMb() {
        return dataMb;
    }

    public Long getSmsCount() {
        return smsCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setUsageDate(LocalDate usageDate) {
        this.usageDate = usageDate;
    }

    public void setCallMinutes(BigDecimal callMinutes) {
        this.callMinutes = callMinutes;
    }

    public void setDataMb(BigDecimal dataMb) {
        this.dataMb = dataMb;
    }

    public void setSmsCount(Long smsCount) {
        this.smsCount = smsCount;
    }
}