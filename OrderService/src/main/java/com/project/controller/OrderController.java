package com.project.controller;

import com.project.service.OrderService;
import com.project.service.OrderServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
//    private final OrderService orderService;
//
//    @Autowired
//    public  OrderController(OrderServiceImpl orderServiceImpl){
//        this.orderService = orderServiceImpl;
//    }
}
