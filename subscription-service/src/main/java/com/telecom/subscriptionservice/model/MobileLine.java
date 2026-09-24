package com.telecom.subscriptionservice.model;

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
import jakarta.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an actual telecom mobile line owned by a Customer.
 *
 * Important:
 *
 * User/Customer handles identity and customer information.
 * MobileLine represents the actual telecom number/SIM.
 *
 * Example:
 *
 * Customer #2
 *      |
 *      +---- MobileLine
 *              phoneNumber = 9876543210
 *              simNumber   = 89014103211118510720
 *
 * A customer can own multiple MobileLines.
 */
@Entity
@Table(
    name = "mobile_lines",
    indexes = {
        @Index(
            name = "idx_mobile_line_customer_id",
            columnList = "customer_id"
        ),
        @Index(
            name = "idx_mobile_line_customer_status",
            columnList = "customer_id,status"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MobileLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID of the Customer from User Service.
     *
     * This is NOT a JPA foreign key because Customer belongs
     * to a different microservice/database.
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * Actual telecom mobile number.
     *
     * This is unique across the entire Subscription Service.
     */
    @Column(
        name = "phone_number",
        nullable = false,
        unique = true,
        length = 10
    )
    private String phoneNumber;

    /**
     * SIM/ICCID associated with the mobile line.
     *
     * ICCIDs are generally 19-20 digits.
     */
    @Column(
        name = "sim_number",
        nullable = false,
        unique = true,
        length = 20
    )
    private String simNumber;

    /**
     * Current lifecycle state of the mobile line.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 20
    )
    private MobileLineStatus status;

    /**
     * Date on which the mobile line was activated/provisioned.
     */
    @Column(name = "activation_date")
    private LocalDate activationDate;

    /**
     * Date on which the mobile line was permanently deactivated.
     */
    @Column(name = "deactivation_date")
    private LocalDate deactivationDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * Optimistic locking.
     *
     * Prevents two concurrent updates from silently overwriting
     * each other.
     */
    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = MobileLineStatus.ACTIVE;
        }

        if (activationDate == null) {
            activationDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();
    }
}