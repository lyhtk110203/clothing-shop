package com.shop.clothing_shop.controller;

import com.shop.clothing_shop.dto.request.VariantRequest;
import com.shop.clothing_shop.entity.ProductVariant;
import com.shop.clothing_shop.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService variantService;

    @PostMapping("/product/{productId}")
    public ProductVariant create(
            @PathVariable Long productId,
            @RequestBody VariantRequest req
    ) {
        return variantService.createVariant(productId, req);
    }

    @PutMapping("/{id}")
    public ProductVariant update(
            @PathVariable Long id,
            @RequestBody VariantRequest req
    ) {
        return variantService.updateVariant(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        variantService.deleteVariant(id);
    }
}
