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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @Column(name = "tracking_token", unique = true, nullable = false)
    private UUID trackingToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems;

    @PrePersist
    void onCreate() {
        this.orderCode = "ORD-" + System.currentTimeMillis();
        this.trackingToken = UUID.randomUUID();
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }
}