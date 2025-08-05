package com.project.repository;

import com.project.entity.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface ItemRepository extends MongoRepository<Item, Long> {
    List<Item> findByNameContaining(String name);
}
