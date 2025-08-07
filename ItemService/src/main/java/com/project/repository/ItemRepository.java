package com.project.repository;

import com.project.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface ItemRepository extends MongoRepository<Item, Long> {
    List<Item> findByNameContainingIgnoreCase(String name);

    Optional<Item> findByUpc(String upc);

    Optional<Item> findById(Long id);

}
