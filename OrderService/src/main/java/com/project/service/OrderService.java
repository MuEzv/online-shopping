package com.project.service;

import com.project.entity.Order;

import java.util.Optional;

public interface OrderService {

    public Optional<Order> placeOrder(Order order);
}
