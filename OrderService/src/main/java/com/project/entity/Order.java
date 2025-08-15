package com.project.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import java.util.Date;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data

public class Order {
    @Id
    private String orderId;
    private String userId;

    private OrderStatus status;

    private List<ItemDTO> items;
    private double totalPrice;
    private Date createdAt;
    private Date updatedAt;

}
