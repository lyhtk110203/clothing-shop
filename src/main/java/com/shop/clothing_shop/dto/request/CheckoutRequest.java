package com.shop.clothing_shop.dto.request;

import com.shop.clothing_shop.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CheckoutRequest {
//    private UUID cartToken;
    private String customerName;
    private String phone;
    private String address;
    private PaymentMethod paymentMethod;
}

