package com.shop.clothing_shop.repository;

import com.shop.clothing_shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId);

    @Query("""
        select coalesce(sum(oi.quantity), 0)
        from OrderItem oi
        where oi.variant.id = :variantId
    """)
    int getSoldQuantity(@Param("variantId") Long variantId);
}

