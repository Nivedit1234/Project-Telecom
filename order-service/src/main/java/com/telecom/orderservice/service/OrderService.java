package com.telecom.orderservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.telecom.orderservice.client.CatalogServiceClient;
import com.telecom.orderservice.client.CatalogServiceClient.PlanDetails;
import com.telecom.orderservice.client.SubscriptionServiceClient;
import com.telecom.orderservice.client.SubscriptionServiceClient.MobileLineDetails;
import com.telecom.orderservice.client.SubscriptionServiceClient.SubscriptionDetails;
import com.telecom.orderservice.client.UserServiceClient;
import com.telecom.orderservice.client.UserServiceClient.CustomerDetails;
import com.telecom.orderservice.dto.OrderRequest;
import com.telecom.orderservice.dto.OrderResponse;
import com.telecom.orderservice.event.OrderCompletedEvent;
import com.telecom.orderservice.exception.InvalidOrderException;
import com.telecom.orderservice.exception.ResourceNotFoundException;
import com.telecom.orderservice.model.Order;
import com.telecom.orderservice.model.OrderStatus;
import com.telecom.orderservice.model.OrderType;
import com.telecom.orderservice.producer.OrderEventProducer;
import com.telecom.orderservice.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    private final SubscriptionServiceClient subscriptionServiceClient;
    private final CatalogServiceClient catalogServiceClient;
    private final OrderEventProducer orderEventProducer;

    public OrderService(
            OrderRepository orderRepository,
            UserServiceClient userServiceClient,
            SubscriptionServiceClient subscriptionServiceClient,
            CatalogServiceClient catalogServiceClient,
            OrderEventProducer orderEventProducer) {

        this.orderRepository = orderRepository;
        this.userServiceClient = userServiceClient;
        this.subscriptionServiceClient = subscriptionServiceClient;
        this.catalogServiceClient = catalogServiceClient;
        this.orderEventProducer = orderEventProducer;
    }

    public OrderResponse createOrder(OrderRequest request) {

        validateOrderRequest(request);

        Long customerId = request.getCustomerId();
        Long mobileLineId = request.getMobileLineId();
        Long subscriptionId = request.getSubscriptionId();
        Long requestedPlanId = request.getRequestedPlanId();
        OrderType orderType = request.getType();

        // =====================================================
        // STEP 1 - Validate customer
        // =====================================================

        CustomerDetails customer =
                userServiceClient.getCustomer(customerId);

        // =====================================================
        // STEP 2 - Validate mobile line
        // =====================================================

        MobileLineDetails mobileLine =
                subscriptionServiceClient.getMobileLine(mobileLineId);

        if (!mobileLine.customerId().equals(customer.id())) {

            throw new InvalidOrderException(
                    "Mobile line does not belong to customer");
        }

        // =====================================================
        // STEP 3 - Validate requested plan
        // =====================================================

        if (requestedPlanId != null) {

            PlanDetails plan =
                    catalogServiceClient.getPlan(requestedPlanId);

            if (Boolean.FALSE.equals(plan.active())) {

                throw new InvalidOrderException(
                        "Requested plan is not active");
            }
        }

        // =====================================================
        // STEP 4 - Validate subscription
        // =====================================================

        SubscriptionDetails subscription = null;

        if (subscriptionId != null) {

            subscription =
                    subscriptionServiceClient
                            .getSubscription(subscriptionId);

            if (!subscription.mobileLineId()
                    .equals(mobileLineId)) {

                throw new InvalidOrderException(
                        "Subscription does not belong to mobile line");
            }

            validateSubscriptionState(
                    orderType,
                    subscription.status());

            if (orderType == OrderType.PLAN_CHANGE
                    && requestedPlanId.equals(subscription.planId())) {

                throw new InvalidOrderException(
                        "Requested plan is already assigned to the subscription");
            }
        }

        // =====================================================
        // STEP 5 - Create order in PROCESSING
        // =====================================================

        Order order = new Order();

        order.setCustomerId(customerId);
        order.setMobileLineId(mobileLineId);
        order.setSubscriptionId(subscriptionId);
        order.setType(orderType);
        order.setStatus(OrderStatus.PROCESSING);
        order.setRequestedPlanId(requestedPlanId);

        LocalDateTime now = LocalDateTime.now();

        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        Order savedOrder =
                orderRepository.save(order);

        // =====================================================
        // STEP 6 - Perform actual business operation
        // =====================================================

        try {

            if (orderType == OrderType.PLAN_CHANGE) {

                SubscriptionDetails updatedSubscription =
                        subscriptionServiceClient.changePlan(
                                subscriptionId,
                                requestedPlanId);

                if (updatedSubscription == null) {

                    throw new InvalidOrderException(
                            "Subscription Service returned no result after plan change");
                }

                // Make sure the returned subscription actually
                // has the requested plan.
                if (!requestedPlanId.equals(
                        updatedSubscription.planId())) {

                    throw new InvalidOrderException(
                            "Subscription Service did not confirm the requested plan change");
                }
            }

            /*
             * Other order types are not executing their actual
             * downstream business operation yet.
             *
             * For now, PLAN_CHANGE is the completed end-to-end
             * business flow.
             */
            else {

                throw new InvalidOrderException(
                        "Business operation for order type "
                                + orderType
                                + " is not implemented yet");
            }

        } catch (RuntimeException exception) {

            // =================================================
            // BUSINESS OPERATION FAILED
            // =================================================

            savedOrder.setStatus(OrderStatus.FAILED);
            savedOrder.setUpdatedAt(LocalDateTime.now());

            orderRepository.save(savedOrder);

            throw exception;
        }

        // =====================================================
        // STEP 7 - Business operation succeeded
        // =====================================================

        savedOrder.setStatus(OrderStatus.COMPLETED);
        savedOrder.setUpdatedAt(LocalDateTime.now());

        Order completedOrder =
                orderRepository.save(savedOrder);

        // =====================================================
        // STEP 8 - Publish successful business event
        // =====================================================

        OrderCompletedEvent event =
                new OrderCompletedEvent(
                        completedOrder.getId(),
                        completedOrder.getCustomerId(),
                        completedOrder.getMobileLineId(),
                        completedOrder.getSubscriptionId(),
                        completedOrder.getType().name(),
                        completedOrder.getRequestedPlanId(),
                        completedOrder.getCreatedAt().toString()
                );

        orderEventProducer.publishOrderCompleted(event);

        return toResponse(completedOrder);
    }

    // =========================================================
    // REQUEST VALIDATION
    // =========================================================

    private void validateOrderRequest(
            OrderRequest request) {

        if (request == null) {

            throw new InvalidOrderException(
                    "Order request is required");
        }

        if (request.getCustomerId() == null) {

            throw new InvalidOrderException(
                    "Customer ID is required");
        }

        if (request.getMobileLineId() == null) {

            throw new InvalidOrderException(
                    "Mobile line ID is required");
        }

        if (request.getType() == null) {

            throw new InvalidOrderException(
                    "Order type is required");
        }

        switch (request.getType()) {

            case ACTIVATION:

                if (request.getSubscriptionId() != null) {

                    throw new InvalidOrderException(
                            "Activation order cannot contain subscription ID");
                }

                if (request.getRequestedPlanId() == null) {

                    throw new InvalidOrderException(
                            "Activation order requires requested plan ID");
                }

                break;

            case PLAN_CHANGE:

                if (request.getSubscriptionId() == null) {

                    throw new InvalidOrderException(
                            "Plan change order requires subscription ID");
                }

                if (request.getRequestedPlanId() == null) {

                    throw new InvalidOrderException(
                            "Plan change order requires requested plan ID");
                }

                break;

            case SUSPENSION:
            case RESUMPTION:
            case CANCELLATION:

                if (request.getSubscriptionId() == null) {

                    throw new InvalidOrderException(
                            request.getType()
                                    + " order requires subscription ID");
                }

                if (request.getRequestedPlanId() != null) {

                    throw new InvalidOrderException(
                            request.getType()
                                    + " order cannot contain requested plan ID");
                }

                break;

            default:

                throw new InvalidOrderException(
                        "Unsupported order type");
        }
    }

    // =========================================================
    // SUBSCRIPTION STATE VALIDATION
    // =========================================================

    private void validateSubscriptionState(
            OrderType orderType,
            String subscriptionStatus) {

        if (subscriptionStatus == null) {

            throw new InvalidOrderException(
                    "Subscription status is missing");
        }

        switch (orderType) {

            case SUSPENSION:

                if (!"ACTIVE".equalsIgnoreCase(
                        subscriptionStatus)) {

                    throw new InvalidOrderException(
                            "Only ACTIVE subscriptions can be suspended");
                }

                break;

            case RESUMPTION:

                if (!"SUSPENDED".equalsIgnoreCase(
                        subscriptionStatus)) {

                    throw new InvalidOrderException(
                            "Only SUSPENDED subscriptions can be resumed");
                }

                break;

            case CANCELLATION:

                if ("CANCELLED".equalsIgnoreCase(
                        subscriptionStatus)) {

                    throw new InvalidOrderException(
                            "Subscription is already cancelled");
                }

                break;

            default:
                break;
        }
    }

    // =========================================================
    // GET ORDER
    // =========================================================

    public OrderResponse getOrderById(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: "
                                                + id));

        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByCustomerId(
            Long customerId) {

        return orderRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByMobileLineId(
            Long mobileLineId) {

        return orderRepository
                .findByMobileLineId(mobileLineId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersBySubscriptionId(
            Long subscriptionId) {

        return orderRepository
                .findBySubscriptionId(subscriptionId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> getOrdersByStatus(
            OrderStatus status) {

        return orderRepository
                .findByStatus(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // =========================================================
    // MANUAL STATUS UPDATE
    // =========================================================

    public OrderResponse updateOrderStatus(
            Long id,
            OrderStatus newStatus) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with id: "
                                                + id));

        validateStatusTransition(
                order.getStatus(),
                newStatus);

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        Order updatedOrder =
                orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    private void validateStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus) {

        if (currentStatus == newStatus) {

            throw new InvalidOrderException(
                    "Order is already in status "
                            + newStatus);
        }

        if (currentStatus == OrderStatus.COMPLETED
                || currentStatus == OrderStatus.FAILED
                || currentStatus == OrderStatus.CANCELLED) {

            throw new InvalidOrderException(
                    "Cannot change status of a terminal order");
        }

        if (currentStatus == OrderStatus.CREATED
                && (newStatus == OrderStatus.PROCESSING
                || newStatus == OrderStatus.CANCELLED)) {

            return;
        }

        if (currentStatus == OrderStatus.PROCESSING
                && (newStatus == OrderStatus.COMPLETED
                || newStatus == OrderStatus.FAILED
                || newStatus == OrderStatus.CANCELLED)) {

            return;
        }

        throw new InvalidOrderException(
                "Invalid order status transition from "
                        + currentStatus
                        + " to "
                        + newStatus);
    }

    // =========================================================
    // RESPONSE MAPPING
    // =========================================================

    private OrderResponse toResponse(
            Order order) {

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getMobileLineId(),
                order.getSubscriptionId(),
                order.getType(),
                order.getStatus(),
                order.getRequestedPlanId(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}