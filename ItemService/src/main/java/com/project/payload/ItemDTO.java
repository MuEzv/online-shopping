package com.project.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String upc;
    private List<String> images;
    private Integer availableInventory;
    //private Date createdAt;
    private Date updatedAt;
    //private Map<String, String> metadata;
}
