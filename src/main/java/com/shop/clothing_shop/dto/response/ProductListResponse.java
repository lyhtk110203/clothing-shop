package com.shop.clothing_shop.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductListResponse {

    private Long id;
    private String name;
    private String thumbnail;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private boolean inStock;
    private LocalDateTime createdAt;
}

