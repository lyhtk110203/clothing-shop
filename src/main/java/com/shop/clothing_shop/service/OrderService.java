package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.response.OrderResponse;
import com.shop.clothing_shop.entity.Order;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.exception.BusinessException;
import com.shop.clothing_shop.mapper.OrderMapper;
import com.shop.clothing_shop.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderResponse getByTrackingToken(UUID token) {
        Order order = orderRepository.findByTrackingToken(token)
                .orElseThrow(() -> new BusinessException("Order not found"));
        return OrderMapper.toResponse(order);
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    public List<OrderResponse> getByDateRange(
            LocalDateTime from,
            LocalDateTime to
    ) {
        return orderRepository.findByCreatedAtBetween(from, to)
                .stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    public OrderResponse updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("Order not found"));

        order.setStatus(status);
        return OrderMapper.toResponse(order);
    }

    public OrderResponse cancel(UUID trackingToken) {
        Order order = orderRepository.findByTrackingToken(trackingToken)
                .orElseThrow(() -> new BusinessException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new BusinessException(
                    "Cannot cancel order in status " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        return OrderMapper.toResponse(order);
    }
}
