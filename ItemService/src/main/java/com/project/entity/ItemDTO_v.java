package com.project.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter

public class ItemDTO_v {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String upc;
    private List<String> images;
    private Map<String, String> metadata;
    private Integer totalInventory; // 总库存
    private Integer reservedInventory; // 预留库存
    private Integer availableInventory; // 可用库存
    private Date createdAt;
    private Date updatedAt;

}
