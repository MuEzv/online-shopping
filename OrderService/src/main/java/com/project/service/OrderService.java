package com.project.service;

import com.project.entity.Order;
import com.project.entity.OrderStatus;
import com.project.entity.Payment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

public interface OrderService {

    public Optional<Order> placeOrder(Order order);
    public  Optional<Order> updateOrderStatus(String id, OrderStatus status);
    public Optional<Order> updateOrder(Order order);
    public int deleteOrder(String id);
    public Optional<Order>  findOrderById(String orderId);
    public Optional<Order> completeOrder(String orderId, String paymentId);
    public Optional<Payment> processPayment(Payment payment);
    public void dealWithPayment(Order order);
}
