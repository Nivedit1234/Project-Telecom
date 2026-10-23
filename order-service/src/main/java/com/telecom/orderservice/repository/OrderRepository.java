package com.telecom.orderservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.telecom.orderservice.model.Order;
import com.telecom.orderservice.model.OrderStatus;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(Long customerId);

    List<Order> findByMobileLineId(Long mobileLineId);

    List<Order> findBySubscriptionId(Long subscriptionId);

    List<Order> findByStatus(OrderStatus status);
}