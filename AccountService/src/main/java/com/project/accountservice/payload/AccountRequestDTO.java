package com.project.accountservice.payload;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
