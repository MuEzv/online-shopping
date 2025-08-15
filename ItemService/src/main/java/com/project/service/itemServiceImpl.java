package com.project.service;

import com.project.entity.Inventory;
import com.project.entity.Item;
import com.project.payload.*;
import com.project.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class itemServiceImpl implements ItemService {
    @Autowired
    ItemRepository itemRepository;

    // ItemDTO ====> Item
    private Item toItem(ItemRequestDTO itemDTO) {
        Item item = new Item();
        item.setId(itemDTO.getId());
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
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
    private ItemResponseDTO toResponseDTO(Item item) {
        ItemResponseDTO itemDTO = new ItemResponseDTO();
        itemDTO.setId(item.getId());
        itemDTO.setName(item.getName());
        itemDTO.setDescription(item.getDescription());
        itemDTO.setPrice(item.getPrice());
        itemDTO.setUpc(item.getUpc());
        itemDTO.setImages(item.getImages());
        itemDTO.setAvailableInventory(item.getInventory().getAvailble());
        itemDTO.setUpdatedAt(item.getUpdatedAt());
        return itemDTO;
    }
    @Override
    public ItemDTO createItem(ItemRequestDTO itemDTO) {
        Item item = toItem(itemDTO);
        item.setCreatedAt(new Date());
        item.setUpdatedAt(new Date());
        Item savedItem = itemRepository.save(item);
        return toResponseDTO(savedItem);
    }

    @Override
    public ItemDTO updateItem(Long id, ItemRequestDTO itemDTO) {
        Item existingItem = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        existingItem.setName(itemDTO.getName());
        existingItem.setDescription(itemDTO.getDescription());
        existingItem.setPrice(itemDTO.getPrice());
        existingItem.setUpc(itemDTO.getUpc());
        existingItem.setImages(itemDTO.getImages());
        existingItem.setMetadata(itemDTO.getMetadata());
        // Ensure Inventory is not null
        if (existingItem.getInventory() == null) {
            existingItem.setInventory(new Inventory());
        }
        existingItem.getInventory().setTotal(itemDTO.getTotalInventory());
        existingItem.getInventory().setAvailble(itemDTO.getAvailableInventory());
        existingItem.getInventory().setReserved(itemDTO.getReservedInventory());
        existingItem.setUpdatedAt(new Date());
        Item updatedItem = itemRepository.save(existingItem);
        return toResponseDTO(updatedItem);
    }

    @Override
    public ItemDTO deleteItembyId(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        itemRepository.delete(item);
        return toResponseDTO(item);
    }

    @Override
    public List<ItemDTO> getAllItems() {
        List<Item> items = itemRepository.findAll();
        List<ItemDTO> itemDTOs = new ArrayList<>();
        for (Item item : items) {
            itemDTOs.add(toResponseDTO(item));
        }
        return itemDTOs;
    }

    @Override
    public ItemDTO getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        return toResponseDTO(item);
    }

    @Override
    public ItemDTO getItemByUpc(String upc) {
        Item item = itemRepository.findByUpc(upc)
                .orElseThrow(() -> new RuntimeException("Item not found with UPC: " + upc));
        return toResponseDTO(item);
    }

    @Override
    public List<ItemDTO> searchItemsByName(String name) {
        List<Item> items = itemRepository.findByNameContainingIgnoreCase(name);
        List<ItemDTO> itemDTOs = new ArrayList<>();
        for (Item item : items) {
            itemDTOs.add(toResponseDTO(item));
        }
        return itemDTOs;
    }

}
