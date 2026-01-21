package com.shop.clothing_shop.mapper;

import com.shop.clothing_shop.dto.response.CartItemResponse;
import com.shop.clothing_shop.dto.response.CartResponse;
import com.shop.clothing_shop.entity.Cart;
import com.shop.clothing_shop.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public class CartMapper {

    public static CartResponse toResponse(Cart cart) {

        List<CartItemResponse> items =
                cart.getItems() == null
                        ? List.of()
                        : cart.getItems().stream()
                        .map(item -> CartItemResponse.builder()
                                .variantId(item.getVariant().getId())
                                .sku(item.getVariant().getSku())
                                .price(item.getVariant().getPrice())
                                .quantity(item.getQuantity())
                                .subtotal(
                                        item.getVariant().getPrice()
                                                .multiply(BigDecimal.valueOf(item.getQuantity()))
                                )
                                .build())
                        .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartToken(cart.getCartToken())
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }
}
