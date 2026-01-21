package com.shop.clothing_shop.mapper;

import com.shop.clothing_shop.dto.response.ProductListResponse;
import com.shop.clothing_shop.entity.Product;
import com.shop.clothing_shop.entity.ProductImage;
import com.shop.clothing_shop.entity.ProductVariant;
import com.shop.clothing_shop.enums.VariantStatus;

import java.math.BigDecimal;

public class ProductMapper {

    public static ProductListResponse toListResponse(Product product) {

        BigDecimal minPrice = product.getVariants() == null
                ? BigDecimal.ZERO
                : product.getVariants().stream()
                .filter(v -> v.getStatus() == VariantStatus.ACTIVE)
                .map(ProductVariant::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);


        BigDecimal maxPrice = product.getVariants().stream()
                .filter(v -> v.getStatus() == VariantStatus.ACTIVE)
                .map(ProductVariant::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        boolean inStock = product.getVariants().stream()
                .filter(v -> v.getStatus() == VariantStatus.ACTIVE)
                .anyMatch(v -> v.getStockQuantity() > 0);

        String thumbnail = product.getImages().stream()
                .filter(ProductImage::isThumbnail)
                .findFirst()
                .map(ProductImage::getImageUrl)
                .orElse(null);

        return ProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .thumbnail(product.getThumbnailUrl())
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .inStock(inStock)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
