package com.project.itemservice;

import com.project.entity.Inventory;
import com.project.entity.Item;
import com.project.entity.ItemDTO;
import com.project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class itemServiceImpl implements ItemService {
    @Autowired
    ItemRepository itemRepository;

    // ItemDTO ====> Item
    private Item toItem(ItemDTO itemDTO) {
        Item item = new Item();
        item.setId(itemDTO.getId());
        item.setName(itemDTO.getName());
        item.setDescription(item.getDescription());
        item.setPrice(itemDTO.getPrice());
        item.setImages(itemDTO.getImages());
        item.setMetadata(itemDTO.getMetadata());
        item.setUpc(itemDTO.getUpc());
        Inventory inventory = new Inventory();
        inventory.setTotal(itemDTO.getTotalInventory());
        inventory.setAvailble(itemDTO.getAvailableInventory());
        inventory.setReserved(itemDTO.getReservedInventory());
        item.setInventory(inventory);
        item.setCreatedAt(itemDTO.getCreatedAt());
        item.setUpdatedAt(itemDTO.getUpdatedAt());
        return item;
    }

    // Item ===> ItemDTO
    private ItemDTO toItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName(item.getName());
        itemDTO.setId(item.getId());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setUpc(item.getUpc());
        itemDTO.setImages(item.getImages());
        itemDTO.setMetadata(item.getMetadata());
        itemDTO.setCreatedAt(item.getCreatedAt());
        itemDTO.setUpdatedAt(item.getUpdatedAt());
        Inventory inventory = item.getInventory();
        itemDTO.setTotalInventory(inventory.getTotal());
        itemDTO.setAvailableInventory(inventory.getAvailble());
        itemDTO.setReservedInventory(inventory.getReserved());
        return itemDTO;
    }

    @Override
    public ItemDTO createItem(ItemDTO itemDTO) {
        Item item = toItem(itemDTO);
        item.setCreatedAt(new Date());
        item.setUpdatedAt(new Date());
        Item savedItem = itemRepository.save(item);
        return toItemDTO(savedItem);
    }
}
