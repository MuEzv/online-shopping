package com.project.service;

import com.project.entity.Inventory;
import com.project.entity.Item;
import com.project.service.ItemServiceImpl;
import com.project.payload.ItemDTO;
import com.project.payload.ItemRequestDTO;
import com.project.payload.ItemResponseDTO;
import com.project.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


class ItemServiceImplTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }


    public
    @Test
    void createItem() {
        ItemRequestDTO itemRequestDTO = new ItemRequestDTO();
        itemRequestDTO.setName("Test Item");
        itemRequestDTO.setDescription("Test Description");
        itemRequestDTO.setPrice(100.0);
        itemRequestDTO.setTotalInventory(10);
        itemRequestDTO.setAvailableInventory(8);
        itemRequestDTO.setReservedInventory(2);

        // Initialize Item
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(100.0);
        Inventory inventory = new Inventory(); // initialize Inventory
        inventory.setTotal(10);
        inventory.setAvailble(8);
        inventory.setReserved(2);
        item.setInventory(inventory); // set Inventory

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDTO responseDTO = itemService.createItem(itemRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("Test Item", responseDTO.getName());
        assertEquals("Test Description", responseDTO.getDescription());
        assertEquals(100.0, responseDTO.getPrice());
        assertEquals(8, responseDTO.getAvailableInventory());
        verify(itemRepository, times(1)).save(any(Item.class));

    }

    @Test
    void updateItem_updateExistingItem() {
        // Received item to be updated
        ItemRequestDTO itemRequestDTO = new ItemRequestDTO();
        itemRequestDTO.setName("Updated Item");
        itemRequestDTO.setDescription("Updated Description");
        itemRequestDTO.setPrice(200.0);
        itemRequestDTO.setTotalInventory(20);
        itemRequestDTO.setAvailableInventory(15);
        itemRequestDTO.setReservedInventory(5);

        // existing item in the Repo
        Item existingItem = new Item();
        existingItem.setId(1L);
        existingItem.setName("Original Item");
        existingItem.setDescription("Original Description");
        existingItem.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setTotal(10);
        inventory.setAvailble(8);
        inventory.setReserved(2);
        existingItem.setInventory(inventory);

        // Updated item to be saved
        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Updated Item");
        updatedItem.setDescription("Updated Description");
        updatedItem.setPrice(200.0);
        Inventory updatedInventory = new Inventory();
        updatedInventory.setTotal(20);
        updatedInventory.setAvailble(15);
        updatedInventory.setReserved(5);
        updatedItem.setInventory(updatedInventory);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        ItemDTO responseDTO = itemService.updateItem(1L, itemRequestDTO);

        assertNotNull(itemRequestDTO);
        assertEquals("Updated Item", responseDTO.getName());
        assertEquals("Updated Description", responseDTO.getDescription());
        assertEquals(200.0, responseDTO.getPrice());
        assertEquals(15, responseDTO.getAvailableInventory());

        verify(itemRepository, times(1)).save(existingItem);
    }

    @Test
    void deleteItembyId() {
        // Initialize Item
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(100.0);
        Inventory inventory = new Inventory(); // initialize Inventory
        inventory.setTotal(10);
        inventory.setAvailble(8);
        inventory.setReserved(2);
        item.setInventory(inventory); // set Inventory

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDTO responseDTO = itemService.deleteItembyId(1L);

        assertNotNull(responseDTO);
        assertEquals("Test Item", responseDTO.getName());
        assertEquals("Test Description", responseDTO.getDescription());
        assertEquals(100.0, responseDTO.getPrice());
        assertEquals(8, responseDTO.getAvailableInventory());

        verify(itemRepository, times(1)).delete(item);

    }

    @Test
    void getAllItems() {

        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        item1.setDescription("Description 1");
        item1.setPrice(100.0);
        Inventory inventory1 = new Inventory();
        inventory1.setTotal(10);
        inventory1.setAvailble(8);
        inventory1.setReserved(2);
        item1.setInventory(inventory1);

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Item 2");
        item2.setDescription("Description 2");
        item2.setPrice(200.0);
        Inventory inventory2 = new Inventory();
        inventory2.setTotal(20);
        inventory2.setAvailble(15);
        inventory2.setReserved(5);
        item2.setInventory(inventory2);

        List<Item> items = Arrays.asList(item1, item2);
        when(itemRepository.findAll()).thenReturn(items);

        List<ItemDTO> responseDTOs = itemService.getAllItems();

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        assertEquals("Item 1", responseDTOs.get(0).getName());
        assertEquals("Item 2", responseDTOs.get(1).getName());

        verify(itemRepository, times(1)).findAll();

    }

    @Test
    void getItemById_shouldReturnItemDTO() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setTotal(10);
        inventory.setAvailble(8);
        inventory.setReserved(2);
        item.setInventory(inventory);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDTO responseDTO = itemService.getItemById(1L);

        assertNotNull(responseDTO);
        assertEquals("Test Item", responseDTO.getName());
        assertEquals("Test Description", responseDTO.getDescription());
        assertEquals(100.0, responseDTO.getPrice());
        assertEquals(8, responseDTO.getAvailableInventory());
    }

    @Test
    void getItemByUpc() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setUpc("12345");
        item.setDescription("Test Description");
        item.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setTotal(10);
        inventory.setAvailble(8);
        inventory.setReserved(2);
        item.setInventory(inventory);

        when(itemRepository.findByUpc("12345")).thenReturn(Optional.of(item));

        ItemDTO responseDTO = itemService.getItemByUpc("12345");

        assertNotNull(responseDTO);
        assertEquals("Test Item", responseDTO.getName());
        assertEquals("Test Description", responseDTO.getDescription());
        assertEquals(100.0, responseDTO.getPrice());
        assertEquals(8, responseDTO.getAvailableInventory());
        assertEquals("12345", responseDTO.getUpc());

        verify(itemRepository, times(1)).findByUpc("12345");
    }

    @Test
    void searchItemsByName() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item1");
        item1.setUpc("12345");
        item1.setDescription("Test Description");
        item1.setPrice(100.0);
        Inventory inventory = new Inventory();
        inventory.setTotal(10);
        inventory.setAvailble(8);
        inventory.setReserved(2);
        item1.setInventory(inventory);

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Test Item2");
        item2.setUpc("12345");
        item2.setDescription("Test Description");
        item2.setPrice(100.0);
        Inventory inventory2 = new Inventory();
        inventory2.setTotal(10);
        inventory2.setAvailble(8);
        inventory2.setReserved(2);
        item2.setInventory(inventory2);

        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findByNameContainingIgnoreCase("Test")).thenReturn(items);


        List<ItemDTO> responseDTOs = itemService.searchItemsByName("Test");

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        assertEquals("Test Item1", responseDTOs.get(0).getName());
        assertEquals("Test Item2", responseDTOs.get(1).getName());

        verify(itemRepository, times(1)).findByNameContainingIgnoreCase("Test");

    }
}