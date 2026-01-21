package com.shop.clothing_shop.dto.response;

import com.shop.clothing_shop.entity.Order;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.enums.PaymentMethod;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderTrackingResponse {

    private String orderCode;
    private OrderStatus status;
    private PaymentMethod paymentMethod;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;

    public static OrderTrackingResponse from(Order order) {
        return OrderTrackingResponse.builder()
                .orderCode(order.getOrderCode())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

