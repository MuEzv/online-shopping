package com.project.feign;

import com.project.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", configuration = com.project.config.FeignConfig.class)
//@FeignClient(name = "account-service")

public interface AccountClient {
    @GetMapping("/account/test/{email}")
    String getEmail(@PathVariable("email") String email);
}