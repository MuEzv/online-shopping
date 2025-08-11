package com.project.service;

import com.project.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

public interface OrderService {

    public Optional<Order> placeOrder(Order order);
    public  Optional<Order> updateOrderStatus(String id, String status);
    public Optional<Order> updateOrder(Order order);
    public int deleteOrder(String id);
}
