package com.telecom.invoiceservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.invoiceservice.dto.InvoiceResponse;
import com.telecom.invoiceservice.service.InvoiceService;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(
            InvoiceService invoiceService) {

        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public InvoiceResponse getInvoiceById(
            @PathVariable Long id) {

        return invoiceService.getInvoiceById(id);
    }

    @GetMapping("/billing/{billingId}")
    public InvoiceResponse getInvoiceByBillingId(
            @PathVariable Long billingId) {

        return invoiceService.getInvoiceByBillingId(
                billingId
        );
    }

    @GetMapping("/customer/{customerId}")
    public List<InvoiceResponse> getInvoicesByCustomerId(
            @PathVariable Long customerId) {

        return invoiceService.getInvoicesByCustomerId(
                customerId
        );
    }

    @GetMapping("/subscription/{subscriptionId}")
    public List<InvoiceResponse> getInvoicesBySubscriptionId(
            @PathVariable Long subscriptionId) {

        return invoiceService.getInvoicesBySubscriptionId(
                subscriptionId
        );
    }
}