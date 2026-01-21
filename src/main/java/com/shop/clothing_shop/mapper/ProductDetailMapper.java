package com.shop.clothing_shop.mapper;

import com.shop.clothing_shop.dto.response.ProductDetailResponse;
import com.shop.clothing_shop.entity.Product;
import com.shop.clothing_shop.entity.ProductVariant;
import com.shop.clothing_shop.entity.VariantImage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductDetailMapper {

    public static ProductDetailResponse toDetail(Product product) {

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .images(
                        product.getImages().stream()
                                .map(img -> img.getImageUrl())
                                .toList()
                )
                .variants(
                        product.getVariants().stream()
                                .map(ProductDetailMapper::mapVariant)
                                .toList()
                )
                .build();
    }

    private static ProductDetailResponse.VariantResponse mapVariant(ProductVariant variant) {

        Map<String, String> attributes =
                variant.getAttributes().stream()
                        .collect(Collectors.toMap(
                                av -> av.getAttribute().getName(),
                                av -> av.getValue()
                        ));

        List<String> images =
                variant.getImages().stream()
                        .map(VariantImage::getImageUrl)
                        .toList();

        return ProductDetailResponse.VariantResponse.builder()
                .id(variant.getId())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .status(variant.getStatus().name())
                .attributes(attributes)
                .images(images)
                .build();
    }
}
