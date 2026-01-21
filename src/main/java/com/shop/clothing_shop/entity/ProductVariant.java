package com.shop.clothing_shop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shop.clothing_shop.enums.VariantStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product_variant")
@Getter @Setter
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String sku;

    private BigDecimal price;

    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    private VariantStatus status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<AttributeValue> attributes;


    @OneToMany(mappedBy = "variant", fetch = FetchType.LAZY)
    private List<VariantImage> images;

    private LocalDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Long version = 0L;
}
