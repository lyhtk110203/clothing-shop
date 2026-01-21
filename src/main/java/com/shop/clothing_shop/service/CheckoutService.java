package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.request.CheckoutRequest;
import com.shop.clothing_shop.entity.*;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.exception.BusinessException;
import com.shop.clothing_shop.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductVariantRepository variantRepo;
    private final StockReservationService reservationService;

    @Transactional
    public Order checkout(UUID cartToken, CheckoutRequest req) {

        // 1. Load cart
        Cart cart = cartRepo.findByCartToken(cartToken)
                .orElseThrow(() -> new BusinessException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cart empty");
        }

        try {
            // 2. Reserve stock
            cart.getItems().forEach(item ->
                    reservationService.reserve(
                            cartToken,
                            item.getVariant().getId(),
                            item.getQuantity()
                    )
            );

            // 3. Create Order ENTITY (❌ KHÔNG DÙNG MAPPER)
            Order order = Order.builder()
                    .orderCode(UUID.randomUUID().toString())
                    .trackingToken(UUID.randomUUID())
                    .customerName(req.getCustomerName())
                    .phone(req.getPhone())
                    .address(req.getAddress())
                    .paymentMethod(req.getPaymentMethod())
                    .status(OrderStatus.CREATED)
                    .createdAt(LocalDateTime.now())
                    .totalAmount(BigDecimal.ZERO) // bắt buộc
                    .build();

            orderRepo.save(order);

            // 4. Create OrderItems + calculate total
            BigDecimal total = BigDecimal.ZERO;

            for (CartItem item : cart.getItems()) {

                BigDecimal subTotal =
                        item.getVariant().getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()));

                total = total.add(subTotal);

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .variant(item.getVariant())
                        .price(item.getVariant().getPrice())
                        .quantity(item.getQuantity())
                        .build();

                orderItemRepo.save(orderItem);
            }

            // 5. Update total amount (🔥 FIX LỖI total_amount NULL)
            order.setTotalAmount(total);
            orderRepo.save(order);

            // 6. Commit stock (LOCK FOR UPDATE)
            for (CartItem item : cart.getItems()) {

                ProductVariant variant =
                        variantRepo.findByIdForUpdate(item.getVariant().getId())
                                .orElseThrow(() -> new BusinessException("Variant not found"));

                int reserved =
                        reservationService.getReservedQuantity(variant.getId());

                int available =
                        variant.getStockQuantity() - reserved;

                if (available < item.getQuantity()) {
                    throw new BusinessException(
                            "Not enough stock for SKU " + variant.getSku()
                    );
                }

                variant.setStockQuantity(
                        variant.getStockQuantity() - item.getQuantity()
                );

                variantRepo.save(variant);
            }

            // 7. Release reservation
            cart.getItems().forEach(item ->
                    reservationService.releaseByVariant(item.getVariant().getId())
            );

            // 8. Clear cart (ĐÚNG)
            cart.getItems().clear();
            cartRepo.save(cart);
            return order;

        } catch (Exception e) {
            log.error("CHECKOUT FAILED", e);
            throw e; // rollback toàn bộ
        }
    }
}
