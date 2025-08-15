package com.project.entity;

public enum OrderStatus {
    CREATED, // Order need to be created
    PROCESSING, // Ready for payment
    UPDATED, // Order need to be updated
    COMPLETED, // Order is completed
    CANCELLED; // Order is cancelled
}
