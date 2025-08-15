package com.project.service;

import com.datastax.astra.client.Collection;
import com.datastax.astra.client.Database;
import com.datastax.astra.client.model.*;
import com.project.config.AstraDBConnection;
import com.project.entity.Payment;
import com.project.entity.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final Database database;
    private final Collection<Payment> paymentCollection;
    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    public PaymentServiceImpl(AstraDBConnection connection) {
        this.database = connection.getDatabase();
        logger.info("Connected to AstraDB");
        this.paymentCollection = database.getCollection("payments", Payment.class);
        if (this.paymentCollection == null) {

            logger.error("paymentCollection is null! Check AstraDB configuration and collection existence.");
            throw new IllegalStateException("AstraDB payments collection not initialized.");
        }
        logger.info("Connected to collecition");
    }

    @Override
    public Payment submitPayment(Payment payment) {
        // Idempotency check
        if (payment.getPaymentId() == null || payment.getPaymentId().isEmpty()) {
            payment.setPaymentId(UUID.randomUUID().toString());
        }
        Optional<Payment> existingPayment = Optional.empty();
        try {
            existingPayment = paymentCollection.findOne(Filters.eq("paymentId", payment.getPaymentId()));
        } catch (NullPointerException npe) {
            logger.warn("AstraDB returned null document for paymentId: {}", payment.getPaymentId());
            // Treat as not found, continue
        } catch (Exception e) {
            logger.error("Error checking for existing payment: {}", e.getMessage());
            throw new RuntimeException("Error checking for existing payment", e);
        }
        if (existingPayment.isPresent()) {
            logger.warn("Payment with ID: {} already exists.", payment.getPaymentId());
            throw new IllegalArgumentException("Payment ID exists: " + payment.getPaymentId());
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCreatedAt(Instant.now());
        payment.setUpdatedAt(payment.getCreatedAt());

        InsertOneResult result = paymentCollection.insertOne(payment);
        logger.info("Payment placed with ID: {}, Result: {}", payment.getPaymentId(), result.getInsertedId());
        return paymentCollection.findById(result.getInsertedId()).orElse(payment);
    }
    @Override
    public Payment updatePayment(String paymentId, Payment payment) {
        payment.setPaymentId(paymentId);
        if (paymentId == null || paymentId.isEmpty()) {
            logger.error("Cannot update payment: paymentId is null or empty.");
            return null;
        }
        Filter filter = Filters.eq("paymentId", paymentId);
        Update update = Update.create()
                .set("orderId", payment.getOrderId())
                .set("userId", payment.getUserId())
                .set("amount", payment.getAmount())
                .set("paymentMethod", payment.getPaymentMethod())
                .set("status", PaymentStatus.UPDATED)
                .set("updatedAt", Instant.now());

        UpdateResult result = paymentCollection.updateOne(filter, update);
        logger.info("UpdateResult: {}", result);
        logger.error("aaa:{}", result.getMatchedCount());

        if (result.getMatchedCount() > 0) {
            logger.info("Payment with ID: {} updated successfully.", paymentCollection.findOne(Filters.eq("paymentId", paymentId)).orElse(null).getAmount());
            return paymentCollection.findOne(Filters.eq("paymentId", paymentId)).orElse(null);
        } else {
            logger.error("Payment with ID: {} not found for update.", paymentId);
            return null;
        }
    }

    @Override
    public Payment reversePayment(String paymentId) {
        if (paymentId == null || paymentId.isEmpty()) {
            logger.error("Cannot update payment: paymentId is null or empty.");
            return null;
        }
        Filter filter = Filters.eq("paymentId", paymentId);
        Update update = Update.create()
                .set("amount", 0.0)
                .set("status", PaymentStatus.REFUNDED)
                .set("updatedAt", Instant.now());

        UpdateResult result = paymentCollection.updateOne(filter, update);
        if (result.getMatchedCount() > 0) {
            logger.info("Payment with ID: {} refunded.", paymentId);
            return paymentCollection.findOne(Filters.eq("paymentId", paymentId)).orElse(null);
        } else {
            logger.error("Payment with ID: {} not found for refund.", paymentId);
            return null;
        }
    }

    @Override
    public PaymentStatus getPaymentStatus(String paymentId) {
        Optional<Payment> payment = paymentCollection.findOne(Filters.eq("paymentId", paymentId));
        return payment.map(Payment::getStatus).orElse(null);
    }

    public Optional<Payment> findPaymentById(String paymentId) {
        return paymentCollection.findOne(Filters.eq("paymentId", paymentId));
    }
}