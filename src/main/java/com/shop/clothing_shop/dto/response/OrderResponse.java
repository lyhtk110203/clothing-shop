package com.shop.clothing_shop.dto.response;

import com.shop.clothing_shop.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private String orderCode;
    private UUID trackingToken;
    private OrderStatus status;

    private String customerName;
    private String phone;
    private String address;
    private String paymentMethod;

    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
