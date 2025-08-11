package com.project.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
}
