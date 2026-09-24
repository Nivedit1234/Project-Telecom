package com.telecom.subscriptionservice.exception;

/**
 * Thrown when a MobileLine state transition is not allowed.
 */
public class InvalidMobileLineStateException
        extends RuntimeException {

    public InvalidMobileLineStateException(String message) {
        super(message);
    }
}