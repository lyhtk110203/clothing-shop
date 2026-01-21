package com.shop.clothing_shop.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddToCartRequest {
//    private String cartToken;
    private Long skuId;
    private Integer quantity;
}
