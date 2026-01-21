package com.shop.clothing_shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "variant_image")
@Getter
@Setter
public class VariantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private Integer sortOrder;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;
}
