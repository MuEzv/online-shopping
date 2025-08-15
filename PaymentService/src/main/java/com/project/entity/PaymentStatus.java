package com.project.entity;

import com.datastax.astra.client.model.Update;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    CANCELLED,
    UPDATED;
}
