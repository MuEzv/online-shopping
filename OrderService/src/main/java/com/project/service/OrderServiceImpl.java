package com.project.service;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.Database;
import com.datastax.astra.client.model.*;
import com.project.client.AccountServiceClient;
import com.project.client.ItemServiceClient;
import com.project.client.PaymentServiceClient;
import com.project.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService{
    private final Database database;
    private final ItemServiceClient itemServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final Collection<Order> orderCollection;
    private final Collection<Payment> paymentCollection;
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    public OrderServiceImpl(com.project.config.AstraDBConnection connection, ItemServiceClient itemServiceClient,
                            PaymentServiceClient paymentServiceClient, AccountServiceClient accountServiceClient) {
        this.database = connection.getDatabase();
        logger.info("Connected to AstraDB");
        this.orderCollection = database.getCollection("orders", Order.class);
        this.paymentCollection = database.getCollection("payments", Payment.class);
        this.itemServiceClient = itemServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.accountServiceClient = accountServiceClient;
    }

    public void listCollections() {
        database.listCollectionNames().forEach(System.out::println);
    }

    @Override
    public Optional<Order> placeOrder(Order order){
        // Check if there are items in the order
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }

        // Check if the user is authorized
        authorizeUser(order.getUserId());

        //Idempotency Check: if the order request exists
        Optional<Order> existingOrder = Optional.empty();
        try {
            existingOrder = orderCollection.findOne(Filters.eq("orderId", order.getOrderId()));
        } catch (NullPointerException npe) {
            logger.warn("AstraDB returned null document for orderId: {}", order.getOrderId());
            // Treat as not found, continue
        } catch (Exception e) {
            logger.error("Error checking for existing order: {}", e.getMessage());
            throw new RuntimeException("Error checking for existing payment", e);
        }
        if (existingOrder.isPresent()) {
            logger.warn("Order with ID: {} already exists.", order.getOrderId());
            throw new IllegalArgumentException("Order ID exists: " + order.getOrderId());
        }
        logger.info("Complete the idempotency check. Proceeding to check item availability.");

        // check inventory for each item in the order
        for(var item : order.getItems()) {
            var itemDetails = itemServiceClient.getItemById(item.getId());
            // check if item exists and has enough quantity
            if (itemDetails == null || itemDetails.getAvailableQuantity() < item.getQuantity()) {
                logger.error("Item not available or insufficient quantity: {}", item.getId());
                throw new RuntimeException("Item not available: " + item.getId());
            }
        }
        logger.info("All items are available. Proceeding to insert the order into the database.");

        // Set order status and save to DB
        order.setStatus(OrderStatus.PROCESSING);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        InsertOneResult result = orderCollection.insertOne(order);
        Optional<Order> insertedOrder = orderCollection.findById(result.getInsertedId());
        if (insertedOrder.isEmpty()) {
            logger.error("Inserted order not found in database. ID: {}", result.getInsertedId());
            return Optional.empty();
        }

        // After placing the order, process the payment
        dealWithPayment(insertedOrder.get());

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
        Update update = Update.create()
                .set("orderId", order.getOrderId())
                .set("status", order.getStatus() == OrderStatus.COMPLETED ? OrderStatus.COMPLETED : OrderStatus.PROCESSING)
                .set("totalPrice", order.getTotalPrice())
                .set("userId", order.getUserId())
                .set("items", order.getItems())
                .set("createdAt", order.getCreatedAt())
                .set("updatedAt", order.getUpdatedAt());

        UpdateResult result = orderCollection.updateOne(filter, update);
        if(result.getMatchedCount() > 0){
            // after update complete, attempt to process payment
            logger.info("Order with ID: {} updated successfully.", order.getOrderId());
            if(order.getStatus() == OrderStatus.PROCESSING)dealWithPayment(order);
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

        if (it.hasNext()) {
            return Optional.ofNullable(it.next());
        } else {
            logger.warn("Order with ID {} not found.", orderId);

            return Optional.empty();
        }
    }

    Boolean authorizeUser(String userId) {
        // Implement user authorization logic here
        // For now, we assume all users are authorized
        return true;
    }

    private Payment constructPayment(Order order){
        logger.info("Constructing payment for Order ID: {}", order.getOrderId());
        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID().toString());
        String userId = order.getUserId();

        // COMMENT ACCOUNT CALL
        AccountRequestDTO user = accountServiceClient.getAccount(userId);
        payment.setPaymentMethod(user.getPaymentMethod());

        payment.setPaymentMethod("Default");
        payment.setUserId(userId);
        payment.setOrderId(order.getOrderId());
        payment.setAmount(order.getTotalPrice());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(Instant.now());
        return payment;
    }

    @Override
    public void dealWithPayment(Order order){
        logger.info("Deal with payment for Order ID: {}", order.getOrderId());
        Payment payment = constructPayment(order);
        Optional<Payment> processedPayment = processPayment(payment);
        if(processedPayment.isPresent()){
            completeOrder(order.getOrderId(), processedPayment.get().getPaymentId());
        }else{
            logger.error("Payment processing failed for Order ID: {}", order.getOrderId());
        }
    }

    // After having payment info, call payment service to process the payment
    @Override
    public Optional<Payment> processPayment(Payment payment){
        if (payment == null || payment.getOrderId() == null) {
            logger.error("Invalid payment details provided.");
            return Optional.empty();
        }

        // Check if the order exists
        Optional<Order> order = findOrderById(payment.getOrderId());
        if (order.isEmpty()) {
            logger.error("Order with ID: {} not found for payment processing.", payment.getOrderId());
            return Optional.empty();
        }

        // Check if the order is in PROCESSING state (ready for payment)
        if(order.get().getStatus() != OrderStatus.PROCESSING) {
            logger.error("Order with ID: {} is not in PROCESSING state, current status: {}", payment.getOrderId(), order.get().getStatus());
            return Optional.empty();
        }

        // Get payment processing message
        String msg = paymentServiceClient.submitPayment(payment);
        if(msg == null || msg.isEmpty()) {
            logger.error("Failed to process payment for Order ID: {}", payment.getOrderId());
            return Optional.empty();
        }

        logger.info(msg);
        logger.info("Payment completed for Order ID: {}", payment.getOrderId());
        // Update the payment status to COMPLETED after successful payment
        payment.setStatus(paymentServiceClient.getPaymentStatus(payment.getPaymentId()));
        if(payment.getStatus() != PaymentStatus.COMPLETED) {
            logger.error("Payment for Order ID: {} is not completed, current status: {}", payment.getOrderId(), payment.getStatus());
            return Optional.empty();
        }
        return Optional.of(payment);
    }


    //Aready have orderId and paymentId, check to complete the order
    @Override
    public Optional<Order> completeOrder(String orderId, String paymentId){
        // Validate orderId and paymentId
        if(orderId == null || orderId.isEmpty() || paymentId == null || paymentId.isEmpty()) {
            logger.error("Order ID or Payment ID cannot be null or empty.");
            return Optional.empty();
        }

        // Check if the order exists
        Optional<Order> currentOrder = findOrderById(orderId);
        if (currentOrder.isEmpty()) {
            logger.error("Order with ID: {} not found.", orderId);
            return Optional.empty();
        }

        // Check if the payment exists and is completed
        PaymentStatus paymentStatus = paymentServiceClient.getPaymentStatus(paymentId);
        if(paymentStatus != PaymentStatus.COMPLETED){
            logger.error("Payment with ID: {} is not completed, current status: {}", paymentId, paymentStatus);
            return Optional.empty();
        }

        //Check if the item inventory is sufficient
        if(!checkInventory(currentOrder.get())) {
            logger.error("Insufficient inventory for order: {}", orderId);
            return Optional.empty();
        }

        // Deduct item quantities from inventory
        for(ItemDTO item : currentOrder.get().getItems()) {
            ItemDTO itemInStock = itemServiceClient.getItemById(item.getId());
            itemInStock.setQuantity(itemInStock.getQuantity() - item.getQuantity());
            itemInStock.setAvailableQuantity(itemInStock.getAvailableQuantity() - item.getQuantity());
            itemServiceClient.updateItem(itemInStock);
        }

        // Update the order status to COMPLETED
        currentOrder.get().setStatus(OrderStatus.COMPLETED);
        logger.info("Completing order with ID: {}", orderId);
        return updateOrder(currentOrder.get());

    }



}
