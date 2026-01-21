package com.shop.clothing_shop.service;

import com.shop.clothing_shop.entity.ProductVariant;
import com.shop.clothing_shop.entity.StockReservation;
import com.shop.clothing_shop.enums.ReservationStatus;
import com.shop.clothing_shop.exception.BusinessException;
import com.shop.clothing_shop.repository.ProductVariantRepository;
import com.shop.clothing_shop.repository.StockReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductVariantRepository variantRepo;
    private final StockReservationRepository reservationRepo;

    @Transactional
    public void checkAndReserve(ProductVariant variant, int quantity, int minutes) {

        ProductVariant locked =
                variantRepo.findByIdForUpdate(variant.getId())
                        .orElseThrow();

        int reserved =
                reservationRepo.getReservedQuantity(variant.getId());

        int available =
                locked.getStockQuantity() - reserved;

        if (available < quantity) {
            throw new RuntimeException("Out of stock");
        }

        StockReservation reservation = StockReservation.builder()
                .variant(locked)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .expiredAt(LocalDateTime.now().plusMinutes(minutes))
                .build();

        reservationRepo.save(reservation);
    }

    /**
     * Tồn khả dụng = stock - reserved
     */
    public int getAvailableStock(Long variantId) {

        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new BusinessException("Variant not found"));

        int reserved = reservationRepo.getReservedQuantity(variantId);

        return variant.getStockQuantity() - reserved;
    }

    /**
     * Trừ tồn thật (commit)
     */
    @Transactional
    public void deductStock(ProductVariant variant, int qty) {

        ProductVariant locked =
                variantRepo.findByIdForUpdate(variant.getId())
                        .orElseThrow(() -> new BusinessException("Variant not found"));

        if (locked.getStockQuantity() < qty) {
            throw new BusinessException("Insufficient stock");
        }

        locked.setStockQuantity(locked.getStockQuantity() - qty);
    }
}
