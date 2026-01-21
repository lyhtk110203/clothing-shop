package com.shop.clothing_shop.controller;

import com.shop.clothing_shop.dto.request.CheckoutRequest;
import com.shop.clothing_shop.entity.Order;
import com.shop.clothing_shop.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/{cartToken}")
    public Order checkout(
            @PathVariable UUID cartToken,
            @RequestBody CheckoutRequest request
    ) {
        return checkoutService.checkout(cartToken, request);
    }
}
