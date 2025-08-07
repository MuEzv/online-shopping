package com.project.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.*;

@Document(collection = "items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Item {
    @Id
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String upc;
    private List<String> images;
    private Map<String, String> metadata;
    private Inventory inventory;
    private Date createdAt;
    private Date updatedAt;

}
