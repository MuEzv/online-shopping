package com.project.service;

import com.project.entity.Payment;
import com.project.entity.PaymentStatus;

public interface PaymentService {

    public Payment submitPayment(Payment payment);
    public Payment updatePayment(String paymentId, Payment payment);

    public Payment reversePayment(String paymentId);
    public PaymentStatus getPaymentStatus(String paymentId);
}
