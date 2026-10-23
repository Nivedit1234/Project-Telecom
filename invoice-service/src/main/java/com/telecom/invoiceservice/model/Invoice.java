package com.telecom.invoiceservice.model;

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
import jakarta.persistence.Version;

@Entity
@Table(
    name = "invoices",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_invoice_billing_id",
            columnNames = "billing_id"
        ),
        @UniqueConstraint(
            name = "uk_invoice_number",
            columnNames = "invoice_number"
        )
    },
    indexes = {
        @Index(name = "idx_invoice_customer_id", columnList = "customer_id"),
        @Index(name = "idx_invoice_subscription_id", columnList = "subscription_id"),
        @Index(name = "idx_invoice_date", columnList = "invoice_date")
    }
)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "billing_id", nullable = false, unique = true)
    private Long billingId;

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

    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "call_minutes", precision = 19, scale = 3)
    private BigDecimal callMinutes;

    @Column(name = "data_mb", precision = 19, scale = 3)
    private BigDecimal dataMb;

    @Column(name = "sms_count")
    private Long smsCount;

    @Column(name = "call_charges", precision = 19, scale = 2)
    private BigDecimal callCharges;

    @Column(name = "data_charges", precision = 19, scale = 2)
    private BigDecimal dataCharges;

    @Column(name = "sms_charges", precision = 19, scale = 2)
    private BigDecimal smsCharges;

    @Column(name = "plan_charges", precision = 19, scale = 2)
    private BigDecimal planCharges;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private InvoiceStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }

        if (invoiceDate == null) {
            invoiceDate = LocalDate.now();
        }

        if (status == null) {
            status = InvoiceStatus.GENERATED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Long getBillingId() {
        return billingId;
    }

    public void setBillingId(Long billingId) {
        this.billingId = billingId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getMobileLineId() {
        return mobileLineId;
    }

    public void setMobileLineId(Long mobileLineId) {
        this.mobileLineId = mobileLineId;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getBillingPeriodStart() {
        return billingPeriodStart;
    }

    public void setBillingPeriodStart(LocalDate billingPeriodStart) {
        this.billingPeriodStart = billingPeriodStart;
    }

    public LocalDate getBillingPeriodEnd() {
        return billingPeriodEnd;
    }

    public void setBillingPeriodEnd(LocalDate billingPeriodEnd) {
        this.billingPeriodEnd = billingPeriodEnd;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public BigDecimal getCallMinutes() {
        return callMinutes;
    }

    public void setCallMinutes(BigDecimal callMinutes) {
        this.callMinutes = callMinutes;
    }

    public BigDecimal getDataMb() {
        return dataMb;
    }

    public void setDataMb(BigDecimal dataMb) {
        this.dataMb = dataMb;
    }

    public Long getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(Long smsCount) {
        this.smsCount = smsCount;
    }

    public BigDecimal getCallCharges() {
        return callCharges;
    }

    public void setCallCharges(BigDecimal callCharges) {
        this.callCharges = callCharges;
    }

    public BigDecimal getDataCharges() {
        return dataCharges;
    }

    public void setDataCharges(BigDecimal dataCharges) {
        this.dataCharges = dataCharges;
    }

    public BigDecimal getSmsCharges() {
        return smsCharges;
    }

    public void setSmsCharges(BigDecimal smsCharges) {
        this.smsCharges = smsCharges;
    }

    public BigDecimal getPlanCharges() {
        return planCharges;
    }

    public void setPlanCharges(BigDecimal planCharges) {
        this.planCharges = planCharges;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
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
}