package com.project.client;

import com.project.entity.ItemDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service", url = "/item")
public interface ItemServiceClient {

    @GetMapping("/Id/{id}")
    ItemDTO getItemById(@PathVariable("id") Long id);


}
