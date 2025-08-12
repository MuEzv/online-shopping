package com.project.controller;

import com.project.entity.ItemDTO;
import com.project.feign.AccountClient;
import com.project.itemservice.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemService itemService;
    @Autowired
    private AccountClient accountClient;

    @PostMapping("/add")
    public ResponseEntity<ItemDTO> createItem(@RequestBody ItemDTO itemDTO) {
        ItemDTO createdItem = itemService.createItem(itemDTO);
        return ResponseEntity.ok(createdItem);
    }

    @PostMapping("/update")
    public ResponseEntity<ItemDTO> updateItem(@RequestBody ItemDTO itemDTO) {
        ItemDTO updatedItem = itemService.updateItem(itemDTO.getId(), itemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @PostMapping("/delete")
    public ResponseEntity<ItemDTO> deleteItem(@RequestBody Long id) {
        ItemDTO deletedItem = itemService.deleteItembyId(id);
        return ResponseEntity.ok(deletedItem);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<ItemDTO>> getAllItems() {
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/Id/{id}")
    public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
        ItemDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/upc/{upc}")
    public ResponseEntity<ItemDTO> getItemByUpc(@PathVariable String upc) {
        ItemDTO item = itemService.getItemByUpc(upc);
        return ResponseEntity.ok(item);
    }

    @GetMapping("search/{name}")
    public ResponseEntity<List<ItemDTO>> searchItemsByName(@PathVariable String name) {
        List<ItemDTO> items = itemService.searchItemsByName(name);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/account-email/{email}")
    public String getAccountEmail(@PathVariable String email) {
        return accountClient.getEmail(email);
    }
}
