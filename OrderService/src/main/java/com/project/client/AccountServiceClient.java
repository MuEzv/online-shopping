package com.project.client;

import com.project.config.InternalFeignConfig;
import com.project.entity.AccountRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "ACCOUNT-SERVICE",configuration = InternalFeignConfig.class)
public interface AccountServiceClient {
    @GetMapping("/account/{email}")
    AccountRequestDTO getAccount(@PathVariable("email") String email);
}
