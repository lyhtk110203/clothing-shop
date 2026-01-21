package com.shop.clothing_shop.repository;

import com.shop.clothing_shop.entity.StockReservation;
import com.shop.clothing_shop.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Repository
public interface StockReservationRepository
        extends JpaRepository<StockReservation, Long> {

    @Query("""
        select coalesce(sum(r.quantity), 0)
        from StockReservation r
        where r.variant.id = :variantId
          and r.status = 'RESERVED'
          and r.expiredAt > current_timestamp
    """)
    int sumActiveByVariant(@Param("variantId") Long variantId);

    @Query("""
        select coalesce(sum(r.quantity),0)
        from StockReservation r
        where r.variant.id = :variantId
          and r.status = 'RESERVED'
          and r.expiredAt > CURRENT_TIMESTAMP
    """)
    int getReservedQuantity(@Param("variantId") Long variantId);

    @Modifying
    @Query("""
        update StockReservation r
        set r.status = 'EXPIRED'
        where r.variant.id = :variantId
          and r.status = 'RESERVED'
    """)
    void expireByVariant(@Param("variantId") Long variantId);
}