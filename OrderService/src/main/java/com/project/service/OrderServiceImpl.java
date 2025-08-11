package com.project.service;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.Database;
import com.datastax.astra.client.model.*;
import com.project.client.ItemServiceClient;
import com.project.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService{
    private final Database database;
    private final ItemServiceClient itemServiceClient;
    private final Collection<Order> orderCollection;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    public OrderServiceImpl(com.project.config.AstraDBConnection connection, ItemServiceClient itemServiceClient) {
        this.database = connection.getDatabase();
        if (!database.collectionExists("orders")) {
            // If collection doesn't exist
            database.createCollection("orders", Order.class);
        }
        this.orderCollection = database.getCollection("orders", Order.class);
        this.itemServiceClient = itemServiceClient;
    }

    public void listCollections() {
        database.listCollectionNames().forEach(System.out::println);
    }

    @Override
    public Optional<Order> placeOrder(Order order){
        for(var item : order.getItems()) {
            var itemDetails = itemServiceClient.getItemById(item.getId());
            // check if item exists and has enough quantity
            if (itemDetails == null || itemDetails.getAvailableQuantity() < item.getQuantity()) {
                logger.error("Item not available or insufficient quantity: {}", item.getId());
                throw new RuntimeException("Item not available: " + item.getId());
            }
        }

        logger.info("All items are available. Proceeding to insert the order into the database.");
        InsertOneResult result = orderCollection.insertOne(order);
        Optional<Order> insertedOrder = orderCollection.findById(result.getInsertedId());

        //return "Order placed with ID: " + order.getId() + ", Result: " + result.getInsertedId();
        logger.info("Order placed with ID: {}, Result: {}", order.getOrderId(), result.getInsertedId());
        return insertedOrder;
    }

    public Optional<Order> getOrderById(String id) {
        return orderCollection.findById(id);
    }

    public Optional<Order> updateOrder(Order order) {
        Filter filter = Filters.eq("orderId", order.getOrderId());
        Update update = Update.create().set("status", order.getStatus());

        UpdateResult result = orderCollection.updateOne(filter, update);
        if(result.getMatchedCount() > 0){
            return Optional.of(order);
        }else{
            return Optional.empty();
        }
    }

    public int deleteOrder(String id) {
        Filter filter = Filters.eq("orderId", id);
        DeleteResult result = orderCollection.deleteOne(filter);
        logger.info("Order with ID: {} deleted successfully.", id);
        return result.getDeletedCount();
    }





}
