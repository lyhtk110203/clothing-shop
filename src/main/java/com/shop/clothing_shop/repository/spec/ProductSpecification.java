package com.shop.clothing_shop.repository.spec;

import com.shop.clothing_shop.entity.Product;
import com.shop.clothing_shop.entity.ProductVariant;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductSpecification {

    public static Specification<Product> filter(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String keyword,
            LocalDateTime cursor,
            Long cursorId) {
        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (categoryId != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("category").get("id"), categoryId));
            }

            if (keyword != null && !keyword.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("name")),
                                "%" + keyword.toLowerCase() + "%"));
            }

            if (cursor != null) {
                predicate = cb.and(predicate,
                        cb.lessThan(root.get("createdAt"), cursor));
            }

            if (minPrice != null || maxPrice != null) {

                Subquery<Long> sub = query.subquery(Long.class);
                Root<ProductVariant> v = sub.from(ProductVariant.class);

                Predicate pricePredicate = cb.conjunction();

                if (minPrice != null) {
                    pricePredicate = cb.and(pricePredicate,
                            cb.greaterThanOrEqualTo(v.get("price"), minPrice));
                }

                if (maxPrice != null) {
                    pricePredicate = cb.and(pricePredicate,
                            cb.lessThanOrEqualTo(v.get("price"), maxPrice));
                }

                sub.select(v.get("product").get("id"))
                        .where(pricePredicate);

                predicate = cb.and(predicate, root.get("id").in(sub));
            }

            return predicate;
        };
    }
}
