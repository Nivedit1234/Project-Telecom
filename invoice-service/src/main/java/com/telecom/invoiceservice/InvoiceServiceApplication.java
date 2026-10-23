package com.telecom.invoiceservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class InvoiceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                InvoiceServiceApplication.class,
                args
        );
    }
}