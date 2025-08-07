package com.project.itemservice;

import com.project.entity.ItemDTO;
import java.util.*;

public interface ItemService {
    // Define methods that ItemService should implement
    ItemDTO createItem(ItemDTO itemDTO);

    ItemDTO updateItem(Long id, ItemDTO itemDTO);

    ItemDTO deleteItembyId(Long id);

    List<ItemDTO> getAllItems();

    ItemDTO getItemById(Long id);

    ItemDTO getItemByUpc(String upc);

    List<ItemDTO> searchItemsByName(String name);

}
