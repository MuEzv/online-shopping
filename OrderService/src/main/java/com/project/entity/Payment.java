package com.project.entity;

import lombok.Data;

import javax.persistence.Id;
import java.time.Instant;
@Data
public class Payment {
    @Id
    private String paymentId;
    private String orderId;
    private String userId;
    private double amount;
    private String paymentMethod; // e.g., "Credit Card", "PayPal"
    private PaymentStatus status; // e.g., "Pending", "Completed", "Failed"
    private Instant createdAt; // Timestamp of when the payment was created
    private Instant updatedAt; // Timestamp of when the payment was last updated
}
