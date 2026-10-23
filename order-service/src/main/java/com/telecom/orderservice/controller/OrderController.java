package com.telecom.orderservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.telecom.orderservice.dto.OrderRequest;
import com.telecom.orderservice.dto.OrderResponse;
import com.telecom.orderservice.model.OrderStatus;
import com.telecom.orderservice.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(
            OrderService orderService) {

        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody
            OrderRequest request) {

        return orderService.createOrder(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(
            @PathVariable Long id) {

        return orderService.getOrderById(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse>
            getOrdersByCustomer(
                    @PathVariable Long customerId) {

        return orderService
                .getOrdersByCustomerId(customerId);
    }

    @GetMapping("/mobile-line/{mobileLineId}")
    public List<OrderResponse>
            getOrdersByMobileLine(
                    @PathVariable Long mobileLineId) {

        return orderService
                .getOrdersByMobileLineId(
                        mobileLineId);
    }

    @GetMapping("/subscription/{subscriptionId}")
    public List<OrderResponse>
            getOrdersBySubscription(
                    @PathVariable Long subscriptionId) {

        return orderService
                .getOrdersBySubscriptionId(
                        subscriptionId);
    }

    @GetMapping("/status")
    public List<OrderResponse>
            getOrdersByStatus(
                    @RequestParam
                    OrderStatus status) {

        return orderService
                .getOrdersByStatus(status);
    }

    @PatchMapping("/{id}/status")
    public OrderResponse updateOrderStatus(
            @PathVariable Long id,
            @RequestParam
            OrderStatus status) {

        return orderService
                .updateOrderStatus(
                        id,
                        status);
    }
}