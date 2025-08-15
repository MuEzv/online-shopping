package com.project.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestDTO extends ItemDTO {
    private Map<String, String> metadata;
    private Integer totalInventory;    // 总库存
    private Integer reservedInventory; // 预留库存
    private Date createdAt;            // 创建时间
}
