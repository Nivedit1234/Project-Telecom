package com.telecom.invoiceservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.invoiceservice.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByBillingId(Long billingId);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    List<Invoice> findByCustomerIdOrderByInvoiceDateDesc(Long customerId);

    List<Invoice> findBySubscriptionIdOrderByInvoiceDateDesc(Long subscriptionId);
}