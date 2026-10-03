package com.telecom.billingservice.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    name = "billing",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_billing_subscription_period",
            columnNames = {
                "subscription_id",
                "billing_period_start",
                "billing_period_end"
            }
        )
    },
    indexes = {
        @Index(name = "idx_billing_customer", columnList = "customer_id"),
        @Index(name = "idx_billing_mobile_line", columnList = "mobile_line_id"),
        @Index(name = "idx_billing_subscription", columnList = "subscription_id"),
        @Index(name = "idx_billing_period", columnList = "billing_period_start,billing_period_end")
    }
)
public class Billing {

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

    @Column(name = "billing_period_start", nullable = false)
    private LocalDate billingPeriodStart;

    @Column(name = "billing_period_end", nullable = false)
    private LocalDate billingPeriodEnd;

    @Column(name = "call_minutes", nullable = false, precision = 15, scale = 3)
    private BigDecimal callMinutes = BigDecimal.ZERO;

    @Column(name = "data_mb", nullable = false, precision = 18, scale = 3)
    private BigDecimal dataMb = BigDecimal.ZERO;

    @Column(name = "sms_count", nullable = false)
    private Long smsCount = 0L;

    @Column(name = "call_charges", nullable = false, precision = 15, scale = 2)
    private BigDecimal callCharges = BigDecimal.ZERO;

    @Column(name = "data_charges", nullable = false, precision = 15, scale = 2)
    private BigDecimal dataCharges = BigDecimal.ZERO;

    @Column(name = "sms_charges", nullable = false, precision = 15, scale = 2)
    private BigDecimal smsCharges = BigDecimal.ZERO;

    @Column(name = "plan_charges", nullable = false, precision = 15, scale = 2)
    private BigDecimal planCharges = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BillingStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (callMinutes == null) callMinutes = BigDecimal.ZERO;
        if (dataMb == null) dataMb = BigDecimal.ZERO;
        if (smsCount == null) smsCount = 0L;
        if (callCharges == null) callCharges = BigDecimal.ZERO;
        if (dataCharges == null) dataCharges = BigDecimal.ZERO;
        if (smsCharges == null) smsCharges = BigDecimal.ZERO;
        if (planCharges == null) planCharges = BigDecimal.ZERO;
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
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

    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
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

    public BigDecimal getCallCharges() {
        return callCharges;
    }

    public BigDecimal getDataCharges() {
        return dataCharges;
    }

    public BigDecimal getSmsCharges() {
        return smsCharges;
    }

    public BigDecimal getPlanCharges() {
        return planCharges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BillingStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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

    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
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

    public void setCallCharges(BigDecimal callCharges) {
        this.callCharges = callCharges;
    }

    public void setDataCharges(BigDecimal dataCharges) {
        this.dataCharges = dataCharges;
    }

    public void setSmsCharges(BigDecimal smsCharges) {
        this.smsCharges = smsCharges;
    }

    public void setPlanCharges(BigDecimal planCharges) {
        this.planCharges = planCharges;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(BillingStatus status) {
        this.status = status;
    }
}