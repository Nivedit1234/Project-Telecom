package com.telecom.invoiceservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3InvoiceStorageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String invoicePrefix;

    public S3InvoiceStorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucket,
            @Value("${aws.s3.invoice-prefix}") String invoicePrefix) {

        this.s3Client = s3Client;
        this.bucket = bucket;
        this.invoicePrefix = invoicePrefix;
    }

    public String uploadInvoice(
            Long invoiceId,
            String invoiceNumber,
            byte[] pdfBytes) {

        if (bucket == null || bucket.isBlank()) {
            return null;
        }

        String key = invoicePrefix
                + "/"
                + invoiceNumber
                + ".pdf";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(pdfBytes)
        );

        return key;
    }
}