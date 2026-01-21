package com.shop.clothing_shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "attribute_value",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"attribute_id", "value"}
        )
)
@Getter
@Setter
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;
}
