package com.shop.clothing_shop.controller;

import com.shop.clothing_shop.dto.response.OrderResponse;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{trackingToken}")
    public OrderResponse getByTrackingToken(@PathVariable UUID trackingToken) {
        return orderService.getByTrackingToken(trackingToken);
    }

    @GetMapping
    public List<OrderResponse> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/status/{status}")
    public List<OrderResponse> getByStatus(@PathVariable OrderStatus status) {
        return orderService.getByStatus(status);
    }

    @GetMapping("/range")
    public List<OrderResponse> getByDateRange(
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to
    ) {
        return orderService.getByDateRange(from, to);
    }

    @PutMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {
        return orderService.updateStatus(id, status);
    }

    @PutMapping("/{trackingToken}/cancel")
    public OrderResponse cancel(@PathVariable UUID trackingToken) {
        return orderService.cancel(trackingToken);
    }
}
