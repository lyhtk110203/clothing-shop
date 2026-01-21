package com.shop.clothing_shop.mapper;

import com.shop.clothing_shop.dto.response.*;
import com.shop.clothing_shop.entity.*;

import java.math.BigDecimal;
import java.util.List;

public class OrderMapper {

    public static OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items =
                order.getOrderItems().stream()
                        .map(i -> OrderItemResponse.builder()
                                .variantId(i.getVariant().getId())
                                .sku(i.getVariant().getSku())
                                .price(i.getPrice())
                                .quantity(i.getQuantity())
                                .subtotal(
                                        i.getPrice()
                                                .multiply(BigDecimal.valueOf(i.getQuantity()))
                                )
                                .build()
                        )
                        .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .trackingToken(order.getTrackingToken())
                .status(order.getStatus())
                .customerName(order.getCustomerName())
                .phone(order.getPhone())
                .address(order.getAddress())
                .paymentMethod(String.valueOf(order.getPaymentMethod()))
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
