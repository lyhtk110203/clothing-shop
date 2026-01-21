package com.shop.clothing_shop.controller;

import com.shop.clothing_shop.dto.request.ProductRequest;
import com.shop.clothing_shop.dto.response.CursorPageResponse;
import com.shop.clothing_shop.dto.response.ProductDetailResponse;
import com.shop.clothing_shop.dto.response.ProductListResponse;
import com.shop.clothing_shop.entity.Product;
import com.shop.clothing_shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public CursorPageResponse<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String keyword
    ) {
        return productService.getProducts(
                size, cursor, categoryId, minPrice, maxPrice, keyword
        );
    }

    @GetMapping("/{id}")
    public ProductDetailResponse getProductDetail(@PathVariable Long id) {
        return productService.getProductDetail(id);
    }

    @PostMapping("/admin")
    public Product create(@RequestBody ProductRequest req) {
        return productService.createProduct(req);
    }

    @PutMapping("/admin/{id}")
    public Product update(@PathVariable Long id, @RequestBody ProductRequest req) {
        return productService.updateProduct(id, req);
    }

    @DeleteMapping("/admin/{id}")
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}

