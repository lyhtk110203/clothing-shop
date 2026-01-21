package com.shop.clothing_shop.controller;

import com.shop.clothing_shop.dto.request.AddToCartRequest;
import com.shop.clothing_shop.dto.request.UpdateCartItemRequest;
import com.shop.clothing_shop.dto.response.CartResponse;
import com.shop.clothing_shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{token}")
    public CartResponse get(@PathVariable UUID token) {
        return cartService.view(token);
    }

    @PostMapping("/{token}/items")
    public CartResponse add(
            @PathVariable UUID token,
            @RequestBody AddToCartRequest req
    ) {
        return cartService.add(token, req);
    }

    @PutMapping("/{token}/items/{variantId}")
    public CartResponse update(
            @PathVariable UUID token,
            @PathVariable Long variantId,
            @RequestBody UpdateCartItemRequest req
    ) {
        return cartService.update(token, variantId, req);
    }


    @DeleteMapping("/{token}/items/{variantId}")
    public CartResponse remove(
            @PathVariable UUID token,
            @PathVariable Long variantId
    ) {
        return cartService.remove(token, variantId);
    }

    @DeleteMapping("/{token}")
    public void clear(@PathVariable UUID token) {
        cartService.clearCart(token);
    }
}
