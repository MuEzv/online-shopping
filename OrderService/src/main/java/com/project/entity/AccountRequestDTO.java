package com.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestDTO {
    private String email;
    private String password;
    private String username;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
}
