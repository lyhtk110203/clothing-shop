package com.shop.clothing_shop.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProductDetailResponse {

    private Long id;
    private String name;
    private String description;

    private List<String> images;

    private List<VariantResponse> variants;

    @Data
    @Builder
    public static class VariantResponse {
        private Long id;
        private BigDecimal price;
        private Integer stockQuantity;
        private String status;
        private Map<String, String> attributes;
        private List<String> images;
    }
}
