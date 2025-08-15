package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.OrderStatus;
import com.project.service.OrderService;
import com.project.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
   OrderService orderService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final String TOPIC = "order-topic";
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Order Service is running");
    }
    @PostMapping("/place")
    @Transactional
    public ResponseEntity<String> placeOrder(@RequestBody Order order) {
        logger.info("Received order: {}", order);
        order.setCreatedAt(new Date());
        order.setStatus(OrderStatus.CREATED);

        // Send order to Kafka topic
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            order.setOrderId(UUID.randomUUID().toString());
            String orderJson = objectMapper.writeValueAsString(order);

            // Use KafkaTemplate.executeInTransaction for transactional sending
            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(TOPIC, order.getOrderId(), orderJson);
                logger.info("Order sent to Kafka topic: {}", TOPIC);
                return true;
            });


        } catch (Exception e) {
            logger.error("Failed to serialize and send order: {}", order, e);
            return ResponseEntity.status(500).body("Failed to process the order");
        }

        return ResponseEntity.ok("Order sent to Kafka for processing. Order ID: " + order.getOrderId());
    }


    @PostMapping("/update")
    public ResponseEntity<Order> updateOrder(@RequestBody Order order) {
        logger.info("Updating order: {}", order);
        Optional<Order> updatedOrder = orderService.updateOrder(order);
        if(updatedOrder.isEmpty()) {
            logger.error("Failed to update order: {}", order);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedOrder.get());
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable String id) {
        logger.info("Cancelling order with ID: {}", id);
        Optional<Order> cancelledOrder = orderService.cancelOrder(id);
        if (cancelledOrder.isEmpty()) {
            logger.error("Failed to cancel order with ID: {}", id);
            return ResponseEntity.badRequest().body("Order not found or cannot be cancelled");
        }
        return ResponseEntity.ok("Order cancelled successfully. Order ID: " + id);
    }

    @GetMapping("/getAll")
    public  ResponseEntity<List<Order>> getAllOrders() {
        logger.info("Fetching all orders");
        List<Order> orders = orderService.getAllOrders().get();
        if (orders.isEmpty()) {
            logger.warn("No orders found");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }
}
