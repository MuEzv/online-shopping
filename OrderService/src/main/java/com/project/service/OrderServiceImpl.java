package com.project.service;

import com.datastax.astra.client.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements  OrderService{
    private final Database database;

    @Autowired
    public OrderServiceImpl(com.project.config.AstraDBConnection connection) {
        this.database = connection.getDatabase();
    }

    public void listCollections() {
        database.listCollectionNames().forEach(System.out::println);
    }
}
