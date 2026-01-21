package com.shop.clothing_shop.entity;

import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderCode;

    @Column(unique = true, nullable = false)
    private UUID trackingToken;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String customerName;
    private String phone;
    private String address;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @PrePersist
    void prePersist() {
        if (orderCode == null)
            orderCode = "ORD-" + System.currentTimeMillis();
        if (trackingToken == null)
            trackingToken = UUID.randomUUID();
        if (status == null)
            status = OrderStatus.CREATED;
        createdAt = LocalDateTime.now();
    }
}
