package com.project.client;

import com.project.entity.Payment;
import com.project.entity.PaymentStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {

    @PostMapping("/payments/submit")
    String submitPayment(@RequestBody Payment payment);

    @GetMapping("/payments/{paymentId}")
    PaymentStatus getPaymentStatus(@PathVariable String paymentId);
}
