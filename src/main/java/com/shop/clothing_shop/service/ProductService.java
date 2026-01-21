package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.request.ProductRequest;
import com.shop.clothing_shop.dto.response.CursorPageResponse;
import com.shop.clothing_shop.dto.response.ProductDetailResponse;
import com.shop.clothing_shop.dto.response.ProductListResponse;
import com.shop.clothing_shop.entity.Category;
import com.shop.clothing_shop.entity.Product;
import com.shop.clothing_shop.mapper.ProductDetailMapper;
import com.shop.clothing_shop.mapper.ProductMapper;
import com.shop.clothing_shop.repository.CategoryRepository;
import com.shop.clothing_shop.repository.ProductRepository;
import com.shop.clothing_shop.repository.spec.ProductSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;


    public CursorPageResponse<ProductListResponse> getProducts(
            Integer size,
            String cursor,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword
    ) {

        LocalDateTime cursorTime = null;
        Long cursorId = null;

        if (cursor != null) {
            String[] parts = cursor.split("\\|");
            cursorTime = LocalDateTime.parse(parts[0]);
            cursorId = Long.parseLong(parts[1]);
        }

        Specification<Product> spec = ProductSpecification.filter(
                categoryId, minPrice, maxPrice, keyword, cursorTime, cursorId
        );

        Pageable pageable = PageRequest.of(
                0,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        List<Product> products =
                productRepository.findAll(spec, pageable).getContent();

        List<ProductListResponse> responses =
                products.stream()
                        .map(ProductMapper::toListResponse)
                        .toList();

        String nextCursor = products.isEmpty()
                ? null
                : products.get(products.size() - 1).getCreatedAt()
                + "|" + products.get(products.size() - 1).getId();

        return CursorPageResponse.<ProductListResponse>builder()
                .items(responses)
                .nextCursor(nextCursor)
                .hasNext(products.size() == size)
                .build();
    }

    public ProductDetailResponse getProductDetail(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return ProductDetailMapper.toDetail(product);
    }

    @Transactional
    public Product createProduct(ProductRequest req) {

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .category(category)
                .build();

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, ProductRequest req) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(req.getName());
        product.setDescription(req.getDescription());

        return product;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

}
