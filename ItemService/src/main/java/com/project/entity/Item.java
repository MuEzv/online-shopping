package com.project.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")

public class Item {
    @Id
    private Long id;
    private String name;
    private String pictureUrl;
    private Double price;
}
