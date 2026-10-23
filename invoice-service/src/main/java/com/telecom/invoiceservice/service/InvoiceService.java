package com.telecom.invoiceservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.telecom.invoiceservice.dto.BillingGeneratedEvent;
import com.telecom.invoiceservice.dto.InvoiceResponse;
import com.telecom.invoiceservice.exception.ResourceNotFoundException;
import com.telecom.invoiceservice.model.Invoice;
import com.telecom.invoiceservice.model.InvoiceStatus;
import com.telecom.invoiceservice.repository.InvoiceRepository;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PdfInvoiceService pdfInvoiceService;
    private final S3InvoiceStorageService s3InvoiceStorageService;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            PdfInvoiceService pdfInvoiceService,
            S3InvoiceStorageService s3InvoiceStorageService) {

        this.invoiceRepository = invoiceRepository;
        this.pdfInvoiceService = pdfInvoiceService;
        this.s3InvoiceStorageService = s3InvoiceStorageService;
    }

    @Transactional
    public InvoiceResponse generateInvoice(
            BillingGeneratedEvent event) {

        /*
         * Idempotency:
         * One billing record must produce only one invoice.
         */
        Invoice existingInvoice =
                invoiceRepository
                        .findByBillingId(event.billingId())
                        .orElse(null);

        if (existingInvoice != null) {
            return mapToResponse(existingInvoice);
        }

        Invoice invoice = new Invoice();

        invoice.setInvoiceNumber(generateInvoiceNumber());
        invoice.setBillingId(event.billingId());
        invoice.setCustomerId(event.customerId());
        invoice.setMobileLineId(event.mobileLineId());
        invoice.setSubscriptionId(event.subscriptionId());
        invoice.setPlanId(event.planId());

        invoice.setBillingPeriodStart(
                event.billingPeriodStart());

        invoice.setBillingPeriodEnd(
                event.billingPeriodEnd());

        invoice.setInvoiceDate(LocalDate.now());

        invoice.setCallMinutes(event.callMinutes());
        invoice.setDataMb(event.dataMb());
        invoice.setSmsCount(event.smsCount());

        invoice.setCallCharges(event.callCharges());
        invoice.setDataCharges(event.dataCharges());
        invoice.setSmsCharges(event.smsCharges());
        invoice.setPlanCharges(event.planCharges());
        invoice.setTotalAmount(event.totalAmount());

        invoice.setStatus(InvoiceStatus.GENERATED);

        Invoice savedInvoice =
                invoiceRepository.save(invoice);

        try {

            byte[] pdf =
                    pdfInvoiceService.generatePdf(savedInvoice);

            String s3Key =
                    s3InvoiceStorageService.uploadInvoice(
                            savedInvoice.getId(),
                            savedInvoice.getInvoiceNumber(),
                            pdf
                    );

            savedInvoice.setS3Key(s3Key);

            savedInvoice =
                    invoiceRepository.save(savedInvoice);

            return mapToResponse(savedInvoice);

        } catch (Exception exception) {

            savedInvoice.setStatus(InvoiceStatus.FAILED);

            invoiceRepository.save(savedInvoice);

            throw new IllegalStateException(
                    "Failed to generate invoice for billingId="
                            + event.billingId(),
                    exception
            );
        }
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {

        Invoice invoice =
                invoiceRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invoice not found with id: " + id
                                ));

        return mapToResponse(invoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceByBillingId(
            Long billingId) {

        Invoice invoice =
                invoiceRepository
                        .findByBillingId(billingId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Invoice not found for billingId: "
                                                + billingId
                                ));

        return mapToResponse(invoice);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesByCustomerId(
            Long customerId) {

        return invoiceRepository
                .findByCustomerIdOrderByInvoiceDateDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesBySubscriptionId(
            Long subscriptionId) {

        return invoiceRepository
                .findBySubscriptionIdOrderByInvoiceDateDesc(
                        subscriptionId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String generateInvoiceNumber() {

        return "INV-"
                + LocalDate.now()
                    .toString()
                    .replace("-", "")
                + "-"
                + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
    }

    private InvoiceResponse mapToResponse(
            Invoice invoice) {

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getBillingId(),
                invoice.getCustomerId(),
                invoice.getMobileLineId(),
                invoice.getSubscriptionId(),
                invoice.getPlanId(),
                invoice.getBillingPeriodStart(),
                invoice.getBillingPeriodEnd(),
                invoice.getInvoiceDate(),
                invoice.getCallMinutes(),
                invoice.getDataMb(),
                invoice.getSmsCount(),
                invoice.getCallCharges(),
                invoice.getDataCharges(),
                invoice.getSmsCharges(),
                invoice.getPlanCharges(),
                invoice.getTotalAmount(),
                invoice.getS3Key(),
                invoice.getStatus(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}