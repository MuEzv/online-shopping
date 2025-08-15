package com.project.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.entity.Payment;
import com.project.entity.PaymentStatus;
import com.project.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments/")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    private final String TOPIC = "payment-topic";
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/submit")
    @Transactional
    public ResponseEntity<String> submitPayment(@RequestBody Payment payment) {
        try {
            if (payment.getPaymentId() == null || payment.getPaymentId().isEmpty()) {
                payment.setPaymentId(UUID.randomUUID().toString());
            }

            paymentService.submitPayment(payment); // Persist and check idempotency

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String paymentJson = objectMapper.writeValueAsString(payment);

            kafkaTemplate.executeInTransaction(operations -> {
                operations.send(TOPIC, payment.getOrderId(), paymentJson);

                logger.info("Payment sent to Kafka topic: {}", TOPIC);
                return true;
            });
        } catch (Exception e) {
            logger.error("Failed to serialize and send payment: {}", payment, e);
            return ResponseEntity.status(500).body("Failed to process the order");
        }
        return ResponseEntity.ok("Payment sent to Kafka for processing. Payment ID: " + payment.getPaymentId());
    }

    @Transactional
    @PutMapping("/{paymentId}")
    public ResponseEntity<String> updatePayment(@PathVariable String paymentId, @RequestBody Payment payment) {
        try {
            Payment updated = paymentService.updatePayment(paymentId, payment);
            if (updated != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                String paymentJson = objectMapper.writeValueAsString(updated);

                kafkaTemplate.executeInTransaction(operations -> {
                    operations.send(TOPIC, updated.getOrderId(), paymentJson);
                    logger.info("Updated payment sent to Kafka topic: {}", TOPIC);
                    return true;
                });

                return ResponseEntity.ok("Payment updated and sent to Kafka. Payment ID: " + paymentId);
            } else {
                logger.warn("Payment with ID {} not found for update.", paymentId);
                return ResponseEntity.status(404).body("Payment not found for update. Payment ID: " + paymentId);
            }
        } catch (Exception e) {
            logger.error("Failed to update payment: {}", payment, e);
            return ResponseEntity.status(500).body("Failed to update payment. Payment ID: " + paymentId);
        }
    }

    @Transactional
    @PostMapping("/reverse/{paymentId}")
    public ResponseEntity<String> reversePayment(@PathVariable String paymentId) {
        try {
            Payment reversed = paymentService.reversePayment(paymentId);
            if (reversed != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                String paymentJson = objectMapper.writeValueAsString(reversed);

                kafkaTemplate.executeInTransaction(operations -> {
                    operations.send(TOPIC, reversed.getOrderId(), paymentJson);
                    logger.info("Reversed payment sent to Kafka topic: {}", TOPIC);
                    return true;
                });

                return ResponseEntity.ok("Payment reversed and sent to Kafka. Payment ID: " + paymentId);
            } else {
                logger.warn("Payment with ID {} not found for refund.", paymentId);
                return ResponseEntity.status(404).body("Payment not found for refund. Payment ID: " + paymentId);
            }
        } catch (Exception e) {
            logger.error("Failed to reverse payment: {}", paymentId, e);
            return ResponseEntity.status(500).body("Failed to reverse payment. Payment ID: " + paymentId);
        }
    }

    // Payment Lookup
    @GetMapping("/{paymentId}")
    public PaymentStatus getPaymentStatus(@PathVariable String paymentId) {
        return paymentService.getPaymentStatus(paymentId);
    }


}