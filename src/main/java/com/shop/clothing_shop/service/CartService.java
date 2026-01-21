package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.request.AddToCartRequest;
import com.shop.clothing_shop.dto.request.UpdateCartItemRequest;
import com.shop.clothing_shop.dto.response.CartResponse;
import com.shop.clothing_shop.entity.Cart;
import com.shop.clothing_shop.entity.CartItem;
import com.shop.clothing_shop.entity.ProductVariant;
import com.shop.clothing_shop.exception.BusinessException;
import com.shop.clothing_shop.mapper.CartMapper;
import com.shop.clothing_shop.repository.CartItemRepository;
import com.shop.clothing_shop.repository.CartRepository;
import com.shop.clothing_shop.repository.ProductVariantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final ProductVariantRepository variantRepo;
    private final StockReservationService reservationService;

    public CartResponse view(UUID token) {
        Cart cart = getOrCreate(token);
        return CartMapper.toResponse(cart);
    }

    public Cart getOrCreate(UUID token) {
        return cartRepo.findByCartToken(token)
                .orElseGet(() -> cartRepo.save(
                        Cart.builder()
                                .cartToken(token)
                                .expiredAt(LocalDateTime.now().plusDays(1))
                                .build()
                ));
    }

    public CartResponse add(UUID token, AddToCartRequest req) {

        Cart cart = getOrCreate(token);

        ProductVariant variant = variantRepo
                .findByIdForUpdate(req.getSkuId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        int available =
                variant.getStockQuantity()
                        - reservationService.getReservedQuantity(variant.getId());

        if (available < req.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        CartItem item = itemRepo
                .findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .variant(variant)
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + req.getQuantity());
        itemRepo.save(item);

        return CartMapper.toResponse(cart);
    }

    public CartResponse update(UUID token, Long variantId, UpdateCartItemRequest req) {

        Cart cart = getOrCreate(token);

        CartItem item = itemRepo
                .findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (req.getQuantity() <= 0) {
            itemRepo.delete(item);
            return CartMapper.toResponse(cart);
        }

        ProductVariant variant = variantRepo
                .findByIdForUpdate(variantId)
                .orElseThrow();

        int available =
                variant.getStockQuantity()
                        - reservationService.getReservedQuantity(variantId);

        if (available < req.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        item.setQuantity(req.getQuantity());
        return CartMapper.toResponse(cart);
    }

    public CartResponse remove(UUID token, Long variantId) {

        Cart cart = getOrCreate(token);

        itemRepo.findByCartIdAndVariantId(cart.getId(), variantId)
                .ifPresent(item -> {
                    cart.getItems().remove(item); // ✅ QUAN TRỌNG
                    itemRepo.delete(item);
                });

        return CartMapper.toResponse(cart);
    }

    public void clearCart(UUID token) {

        Cart cart = getOrCreate(token);
        itemRepo.deleteByCartId(cart.getId());
        cart.getItems().clear();
    }
}
