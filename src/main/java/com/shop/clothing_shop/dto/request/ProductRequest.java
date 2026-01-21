package com.shop.clothing_shop.dto.request;

import lombok.Data;

@Data
public class ProductRequest {
    private String name;
    private String description;
    private Long categoryId;
}
