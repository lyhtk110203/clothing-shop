package com.shop.clothing_shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gateway;
    private String transactionCode;
    private String status;

    @Lob
    private String rawPayload;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private Payment payment;
}
