package com.shop.clothing_shop.repository;

import com.shop.clothing_shop.entity.Order;
import com.shop.clothing_shop.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    Optional<Order> findByTrackingToken(UUID trackingToken);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to
    );
}
