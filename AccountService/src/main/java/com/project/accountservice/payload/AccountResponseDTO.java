package com.project.accountservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponseDTO {
    private String email;
    private String username;
    private String shippingAddress;
    private String billingAddress;
    private String paymentMethod;
}