package com.shop.clothing_shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CartResponse {
    private UUID cartToken;
    private BigDecimal totalAmount;
    private List<CartItemResponse> items;
}

