package com.project.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "account-service")
public interface AccountServiceClient {
}
