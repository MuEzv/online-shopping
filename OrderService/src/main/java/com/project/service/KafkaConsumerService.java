package com.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.Order;
import com.project.entity.OrderStatus;
import com.project.entity.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    @Autowired
    public KafkaConsumerService(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = "order-topic", groupId = "order-service-group")
    public void consumeOrderMessage(String message, @Header(KafkaHeaders.ACKNOWLEDGMENT)Acknowledgment ack) {
        // Process the incoming message
        System.out.println("Received message: " + message);
        try{
            Order order = objectMapper.readValue(message, Order.class);
            System.out.println("Order ID: " + order.getOrderId());

            // Ensure idempotent processing based on orderId
            Optional<Order> existingOrder = orderService.findOrderById(order.getOrderId());
            if (existingOrder.isPresent()) {
                if (existingOrder.get().getStatus() != null && existingOrder.get().getStatus() == order.getStatus()) {
                    System.out.println("Duplicate message detected, skipping processing for Order ID: " + order.getOrderId());
                    ack.acknowledge();
                    return;
                }
            } else {
                System.out.println("No existing order found for Order ID: " + order.getOrderId());
            }


            logger.info("Processing order by Order status");
            switch (order.getStatus()) {
                case CREATED:
                    logger.info("Processing order creation for Order ID: {}", order.getOrderId());
                    var placedOrder = orderService.placeOrder(order);
                    placedOrder.ifPresentOrElse(
                            o -> System.out.println("Order placed successfully: " + o),
                            () -> System.err.println("Failed to place order: " + order)
                    );
                    break;

                case UPDATED:
                    logger.info("Processing order update for Order ID: {}", order.getOrderId());
                    var updatedOrder = orderService.updateOrder(order);
                    updatedOrder.ifPresentOrElse(
                            o -> System.out.println("Order updated successfully: " + o),
                            () -> System.err.println("Failed to update order: " + order)
                    );
                    break;

                case CANCELLED:
                    logger.info("Processing order cancellation for Order ID: {}", order.getOrderId());
                    order.setStatus(OrderStatus.CANCELLED);
                    var cancelledOrder = orderService.updateOrderStatus(order.getOrderId(), OrderStatus.CANCELLED);
                    cancelledOrder.ifPresentOrElse(
                            o -> System.out.println("Order cancelled successfully: " + o),
                            () -> System.err.println("Failed to cancel order:3" + order)
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

    @KafkaListener(topics = "payment-topic", groupId = "payment-service-group")
    public void consumePaymentMessage(String message, @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        // Process the incoming payment message
        System.out.println("Received payment message: " + message);
        try {
            // Deserialize the payment message if needed
            // it's a JSON string, use ObjectMapper to convert it to a Payment object
             Payment payment = objectMapper.readValue(message, Payment.class);
            System.out.println("Processing payment message: " + message);

            switch(payment.getStatus()){
                case FAILED:
                    // If payment was failed, change the order status to UPDATED,
                    // which means the order need to be updated to construct a valid payment to process
                    Optional<Order> curOrder = orderService.findOrderById(payment.getOrderId());
                    if (curOrder.isPresent()) {
                        Order order = curOrder.get();
                        orderService.updateOrderStatus(order.getOrderId(), OrderStatus.UPDATED);
                        logger.info("Payment failed. Order with ID: {} need to be updated to complete payment." , order.getOrderId());
                    } else {
                        System.err.println("No order found for Order ID: " + payment.getOrderId());
                    }
                    break;
                case COMPLETED:
                    Optional<Order> completedOrder = orderService.completeOrder(payment.getOrderId(), payment.getPaymentId());
                    completedOrder.ifPresentOrElse(
                            o -> System.out.println("Order completed successfully: " + o),
                            () -> System.err.println("Failed to complete order for Payment ID: " + payment.getPaymentId())
                    );
                    break;

                default:
                    System.err.println("Payment waiting for complete " + payment.getPaymentId());
                    break;
            }

            ack.acknowledge(); // Acknowledge the message after processing
        } catch (Exception e) {
            System.err.println("Error processing payment message: " + e.getMessage());
        }
    }
}
