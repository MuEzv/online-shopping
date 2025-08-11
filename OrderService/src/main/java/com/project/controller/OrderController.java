package com.project.controller;

import com.project.service.OrderService;
import com.project.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
   OrderService orderService;
    private final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Order Service is running");
    }
    @PostMapping("/place")
    public ResponseEntity<Optional<Order>> placeOrder(@RequestBody Order order) {
        logger.info("Received order: {}", order);
        Optional<Order> newOrder = orderService.placeOrder(order);
        if(newOrder.isEmpty()) {
            logger.error("Failed to place order: {}", order);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newOrder);
    }

    @PostMapping("/update/{id}/{status}")
    public ResponseEntity<Optional<Order>> updateOrderStatus(@PathVariable String id, @PathVariable String status) {
        logger.info("Updating order status for ID: {}, Status: {}", id, status);
        Optional<Order> updatedOrder = orderService.updateOrderStatus(id, status);
        if(updatedOrder.isEmpty()) {
            logger.error("Failed to update order status for ID: {}", id);
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updatedOrder);
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
}
