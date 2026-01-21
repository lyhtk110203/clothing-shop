package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.request.VariantRequest;
import com.shop.clothing_shop.entity.AttributeValue;
import com.shop.clothing_shop.entity.Product;
import com.shop.clothing_shop.entity.ProductVariant;
import com.shop.clothing_shop.enums.VariantStatus;
import com.shop.clothing_shop.repository.AttributeValueRepository;
import com.shop.clothing_shop.repository.ProductRepository;
import com.shop.clothing_shop.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductVariantService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final AttributeValueRepository attributeValueRepository;

    @Transactional
    public ProductVariant createVariant(Long productId, VariantRequest req) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Set<AttributeValue> attributes =
                new HashSet<>(attributeValueRepository.findAllById(req.getAttributeValueIds()));

        ProductVariant variant = new ProductVariant();
        variant.setProduct(product);
        variant.setPrice(req.getPrice());
        variant.setStockQuantity(req.getStockQuantity());
        variant.setStatus(VariantStatus.ACTIVE);
        variant.setAttributes(attributes);

        return variantRepository.save(variant);
    }

    @Transactional
    public ProductVariant updateVariant(Long id, VariantRequest req) {

        ProductVariant variant = variantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        variant.setPrice(req.getPrice());
        variant.setStockQuantity(req.getStockQuantity());

        return variant;
    }

    @Transactional
    public void deleteVariant(Long id) {
        variantRepository.deleteById(id);
    }
}
