package com.project.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
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
