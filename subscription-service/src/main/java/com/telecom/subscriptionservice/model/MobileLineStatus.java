package com.telecom.subscriptionservice.model;

/**
 * Represents the lifecycle state of a telecom mobile line.
 *
 * ACTIVE       -> The line is currently usable.
 * SUSPENDED    -> The line temporarily cannot be used.
 * DEACTIVATED  -> The line has permanently been retired.
 */
public enum MobileLineStatus {

    ACTIVE,
    SUSPENDED,
    DEACTIVATED
}