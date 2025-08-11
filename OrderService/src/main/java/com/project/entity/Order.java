package com.project.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Column;

import java.util.Date;
import java.util.List;

@Data

public class Order {
    @Id
    private String orderId;
    private String userId;
    private String status;
    private List<ItemDTO> items;
    private double totalPrice;
    private Date createdAt;
    private Date updatedAt;

}
