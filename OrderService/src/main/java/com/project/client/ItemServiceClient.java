package com.project.client;

import com.project.entity.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ITEM-SERVICE")
public interface ItemServiceClient {

    @GetMapping("/items/Id/{id}")
    ItemDTO getItemById(@PathVariable("id") Long id);

    @PostMapping("/items/update")
    ItemDTO updateItem(ItemDTO itemDTO);


}
