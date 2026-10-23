package com.telecom.invoiceservice.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.telecom.invoiceservice.dto.BillingGeneratedEvent;
import com.telecom.invoiceservice.service.InvoiceService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BillingKafkaConsumer {

    private final InvoiceService invoiceService;

    public BillingKafkaConsumer(
            InvoiceService invoiceService) {

        this.invoiceService = invoiceService;
    }

    @KafkaListener(
            topics = "billing-events",
            groupId = "invoice-processing-group"
    )
    public void consumeBillingGeneratedEvent(
            BillingGeneratedEvent event) {

        log.info(
                "Received BillingGeneratedEvent: billingId={}, customerId={}, subscriptionId={}",
                event.billingId(),
                event.customerId(),
                event.subscriptionId()
        );

        InvoiceServiceResult result = process(event);

        log.info(
                "Invoice processing completed: billingId={}, invoiceId={}",
                event.billingId(),
                result.invoiceId()
        );
    }

    private InvoiceServiceResult process(
            BillingGeneratedEvent event) {

        var invoice =
                invoiceService.generateInvoice(event);

        return new InvoiceServiceResult(
                invoice.id()
        );
    }

    private record InvoiceServiceResult(Long invoiceId) {
    }
}