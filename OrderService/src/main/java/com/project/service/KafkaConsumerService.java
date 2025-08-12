package com.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.Order;
import com.project.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaConsumerService {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
    public void consumeMessage(String message, @Header(KafkaHeaders.ACKNOWLEDGMENT)Acknowledgment ack) {
        // Process the incoming message
        System.out.println("Received message: " + message);
        try{
            Order order = objectMapper.readValue(message, Order.class);
            System.out.println("Order ID: " + order.getOrderId());

            // Ensure idempotent processing based on orderId
            Optional<Order> existingOrder = orderService.findOrderById(order.getOrderId());
            if (existingOrder.isPresent()) {
                // Check current status to avoid duplicate processing
                if (existingOrder.get().getStatus() == order.getStatus()) {
                    System.out.println("Duplicate message detected, skipping processing for Order ID: " + order.getOrderId());
                    return; // Skip processing if the status is already updated
                }
            }

            switch (order.getStatus()) {
                case CREATED:
                    var placedOrder = orderService.placeOrder(order);
                    placedOrder.ifPresentOrElse(
                            o -> System.out.println("Order placed successfully: " + o),
                            () -> System.err.println("Failed to place order: " + order)
                    );
                    break;

                case UPDATED:
                    var updatedOrder = orderService.updateOrder(order);
                    updatedOrder.ifPresentOrElse(
                            o -> System.out.println("Order updated successfully: " + o),
                            () -> System.err.println("Failed to update order: " + order)
                    );
                    break;

                case CANCELLED:
                    order.setStatus(OrderStatus.CANCELLED);
                    var cancelledOrder = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.CANCELLED);
                    cancelledOrder.ifPresentOrElse(
                            o -> System.out.println("Order cancelled successfully: " + o),
                            () -> System.err.println("Failed to cancel order: " + order)
                    );
                    break;

                default:
                    System.err.println("Unknown order status: " + order.getStatus());
                    break;

            }
            ack.acknowledge(); // Acknowledge the message after processing
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }

    }
}
