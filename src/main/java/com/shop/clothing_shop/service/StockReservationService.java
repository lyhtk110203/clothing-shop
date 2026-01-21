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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockReservationService {

    private final StockReservationRepository reservationRepo;
    private final ProductVariantRepository variantRepo;

    public int getReservedQuantity(Long variantId) {
        return reservationRepo.getReservedQuantity(variantId);
    }

    public void reserve(UUID cartToken, Long variantId, int qty) {

        ProductVariant variant = variantRepo.findById(variantId)
                .orElseThrow(() -> new BusinessException("Variant not found"));

        StockReservation r = StockReservation.builder()
                .cartToken(cartToken)
                .variant(variant)
                .quantity(qty)
                .status(ReservationStatus.RESERVED)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();

        reservationRepo.save(r);
    }

    public void releaseByVariant(Long variantId) {
        reservationRepo.expireByVariant(variantId);
    }
}
