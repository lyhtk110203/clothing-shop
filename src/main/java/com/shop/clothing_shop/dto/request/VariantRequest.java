package com.shop.clothing_shop.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VariantRequest {
    private BigDecimal price;
    private Integer stockQuantity;
    private List<Long> attributeValueIds;
}
