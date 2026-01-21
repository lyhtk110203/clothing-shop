package com.shop.clothing_shop.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderItemResponse {

    private Long variantId;
    private String sku;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}

