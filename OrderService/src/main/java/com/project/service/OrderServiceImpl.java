package com.project.service;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.Database;
import com.datastax.astra.client.model.*;
import com.project.client.ItemServiceClient;
import com.project.entity.Order;
import com.project.entity.OrderStatus;
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
        logger.info("Connected to AstraDB");
        this.orderCollection = database.getCollection("orders", Order.class);
        this.itemServiceClient = itemServiceClient;
    }

    public void listCollections() {
        database.listCollectionNames().forEach(System.out::println);
    }

    @Override
    public Optional<Order> placeOrder(Order order){
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }

        // Idempotency check:
        Optional<Order> existingOrder = orderCollection.findOne(Filters.eq("orderId", order.getOrderId()));
        if (existingOrder.isPresent()) {
            logger.warn("Order with ID: {} already exists. Try again.", order.getOrderId());
            throw new IllegalArgumentException("Order Id exists: " + order.getOrderId());
        }
        logger.info("Complete the idempotency check. Proceeding to check item availability.");

        for(var item : order.getItems()) {
            var itemDetails = itemServiceClient.getItemById(item.getId());
            // check if item exists and has enough quantity
            if (itemDetails == null || itemDetails.getAvailableQuantity() < item.getQuantity()) {
                logger.error("Item not available or insufficient quantity: {}", item.getId());
                throw new RuntimeException("Item not available: " + item.getId());
            }
        }

        logger.info("All items are available. Proceeding to insert the order into the database.");
        order.setStatus(OrderStatus.PROCESSING);
        InsertOneResult result = orderCollection.insertOne(order);
        Optional<Order> insertedOrder = orderCollection.findById(result.getInsertedId());
        if (insertedOrder.isEmpty()) {
            logger.error("Inserted order not found in database. ID: {}", result.getInsertedId());
            return Optional.empty();
        }

        //return "Order placed with ID: " + order.getId() + ", Result: " + result.getInsertedId();
        logger.info("Order placed with ID: {}, Result: {}", order.getOrderId(), result.getInsertedId());
        return insertedOrder;
    }

    public Optional<Order> getOrderById(String id) {
        Filter filter = Filters.eq("orderId", id);
        Optional<Order> order = orderCollection.findOne(filter);
        if (order.isPresent()) {
            logger.info("Order found: {}", order.get());
            return order;
        } else {
            logger.warn("Order with ID: {} not found.", id);
            return Optional.empty(); // Ensure null is not returned
        }
    }

    @Override
    public Optional<Order> updateOrder(Order order) {
        Filter filter = Filters.eq("orderId", order.getOrderId());
        if(!checkInventory(order)) {
            logger.error("Insufficient inventory for order: {}", order.getOrderId());
            return Optional.empty();
        }
        Update update = Update.create().set("status", order.getStatus());

        UpdateResult result = orderCollection.updateOne(filter, update);
        if(result.getMatchedCount() > 0){
            return Optional.of(order);
        }else{
            return Optional.empty();
        }
    }

    private Boolean checkInventory(Order order){
        for(var item : order.getItems()) {
            var itemDetails = itemServiceClient.getItemById(item.getId());
            // check if item exists and has enough quantity
            if (itemDetails == null || itemDetails.getAvailableQuantity() < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public  Optional<Order> updateOrderStatus(String id, OrderStatus status) {
        Filter filter = Filters.eq("orderId", id);
        Update update = Update.create().set("status", status);

        UpdateResult result = orderCollection.updateOne(filter, update);
        if(result.getMatchedCount() > 0){
            return orderCollection.findById(id);
        }else{
            return Optional.empty();
        }
    }

    @Override
    public int deleteOrder(String id) {
        Filter filter = Filters.eq("orderId", id);
        DeleteResult result = orderCollection.deleteOne(filter);
        logger.info("Order with ID: {} deleted successfully.", id);
        return result.getDeletedCount();
    }

    @Override
    public Optional<Order>  findOrderById(String orderId) {
        Filter filter = Filters.eq("orderId", orderId);
        var it = orderCollection.find(filter, new FindOptions().limit(1)).iterator();
        if(it.hasNext()){
            return Optional.ofNullable(it.next());
        }else{
            logger.warn("Order with ID: {} not found.", orderId);
            return Optional.empty();
        }
    }




}
