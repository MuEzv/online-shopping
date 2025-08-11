package com.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.entity.Order;
import com.project.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

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
    public void consumeMessage(String message) {
        // Process the incoming message
        System.out.println("Received message: " + message);
        try{
            Order order = objectMapper.readValue(message, Order.class);
            System.out.println("Order ID: " + order.getOrderId());

            if(order.getStatus() == OrderStatus.CREATED){
                // Place the order
                var placedOrder = orderService.placeOrder(order);
                if(placedOrder.isPresent()){
                    System.out.println("Order placed successfully: " + placedOrder.get());
                } else {
                    System.err.println("Failed to place order: " + order);
                }
            } else if(order.getStatus() == OrderStatus.UPDATED){
                // Update the order status
                var updatedOrder = orderService.updateOrderStatus(order.getOrderId(), order.getStatus().name());
                if(updatedOrder.isPresent()){
                    System.out.println("Order updated successfully: " + updatedOrder.get());
                } else {
                    System.err.println("Failed to update order: " + order);
                }
            }else if(order.getStatus() == OrderStatus.CANCELLED){
                // cancel the order
                order.setStatus(OrderStatus.CANCELLED);
                var cancelledOrder = orderService.updateOrder(order);
                if(cancelledOrder.isPresent()){
                    System.out.println("Order cancelled successfully: " + cancelledOrder.get());
                } else {
                    System.err.println("Failed to cancel order: " + order);
                }
            }else {
                System.err.println("Unknown order status: " + order.getStatus());
            }
        }catch(Exception e){
            System.err.println("Error processing message: " + e.getMessage());
        }

    }
}
