package com.project.entity;


import lombok.Data;

import java.util.Date;
import java.util.List;

@Data

public class Order {

    private String orderId;
    private String userId;
    private String status;
    private List<ItemDTO> items;
    private double totalPrice;
    private Date createdAt;
    private Date updatedAt;

}
