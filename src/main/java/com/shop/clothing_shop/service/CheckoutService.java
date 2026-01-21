package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.request.CheckoutRequest;
import com.shop.clothing_shop.entity.*;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.enums.PaymentMethod;
import com.shop.clothing_shop.enums.PaymentStatus;
import com.shop.clothing_shop.exception.BusinessException;
import com.shop.clothing_shop.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutService {
        private final CartRepository cartRepo;
        private final OrderRepository orderRepo;
        private final OrderItemRepository orderItemRepo;
        private final ProductVariantRepository variantRepo;
        private final StockReservationService reservationService;
        private final PaymentRepository paymentRepo;
        private final PaymentTransactionRepository paymentTransactionRepo;
        private final EmailService emailService;

        @Transactional
        public Order checkout(UUID cartToken, CheckoutRequest req) {

            Cart cart = cartRepo.findByCartToken(cartToken)
                    .orElseThrow(() -> new BusinessException("Cart not found"));

            if (cart.getItems().isEmpty())
                throw new BusinessException("Cart empty");

            cart.getItems().forEach(i ->
                    reservationService.reserve(
                            cartToken,
                            i.getVariant().getId(),
                            i.getQuantity()
                    )
            );

            Order order = Order.builder()
                    .customerName(req.getCustomerName())
                    .phone(req.getPhone())
                    .email(req.getEmail())
                    .address(req.getAddress())
                    .paymentMethod(req.getPaymentMethod())
                    .totalAmount(BigDecimal.ZERO)
                    .build();

            orderRepo.save(order);

            BigDecimal total = BigDecimal.ZERO;

            for (CartItem item : cart.getItems()) {
                BigDecimal sub =
                        item.getVariant().getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(sub);

                orderItemRepo.save(
                        OrderItem.builder()
                                .order(order)
                                .variant(item.getVariant())
                                .price(item.getVariant().getPrice())
                                .quantity(item.getQuantity())
                                .build()
                );
            }

            order.setTotalAmount(total);

            Payment payment = paymentRepo.save(
                    Payment.builder()
                            .order(order)
                            .method(req.getPaymentMethod())
                            .status(req.getPaymentMethod() == PaymentMethod.COD
                                    ? PaymentStatus.SUCCESS
                                    : PaymentStatus.PENDING)
                            .build()
            );

            if (req.getPaymentMethod() != PaymentMethod.COD) {
                paymentTransactionRepo.save(
                        PaymentTransaction.builder()
                                .gateway(req.getPaymentMethod().name())
                                .transactionCode(order.getOrderCode())
                                .status("PENDING")
                                .payment(payment)
                                .build()
                );
            }

            for (CartItem item : cart.getItems()) {
                ProductVariant v =
                        variantRepo.findByIdForUpdate(item.getVariant().getId())
                                .orElseThrow(() -> new BusinessException("Variant not found"));

                if (v.getStockQuantity() < item.getQuantity())
                    throw new BusinessException("Not enough stock");

                v.setStockQuantity(v.getStockQuantity() - item.getQuantity());
                variantRepo.save(v);
            }

            cart.getItems().clear();
            cartRepo.save(cart);

            emailService.sendOrderTrackingEmail(order, order.getEmail());

            return order;
        }
    }

