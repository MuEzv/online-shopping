package com.project.entity;

import lombok.Data;

@Data
public class ItemDTO {
    private Long Id;
    private String name;
    private double price;
    private int quantity;
    private int availableQuantity;
}
