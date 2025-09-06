package com.project.itemservice;

import com.project.payload.*;

import org.springframework.data.domain.Pageable;
import java.util.*;

public interface ItemService {
    // Define methods that ItemService should implement
    ItemDTO createItem(ItemRequestDTO itemDTO);

    ItemDTO updateItem(Long id, ItemRequestDTO itemDTO);

    ItemDTO deleteItembyId(Long id);

    List<ItemDTO> getAllItems(Pageable pageable);

    ItemDTO getItemById(Long id);

    ItemDTO getItemByUpc(String upc);

    List<ItemDTO> searchItemsByName(String name);

}
