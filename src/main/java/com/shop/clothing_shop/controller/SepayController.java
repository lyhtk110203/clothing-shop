package com.shop.clothing_shop.controller;

import com.shop.clothing_shop.dto.request.SepayIpnRequest;
import com.shop.clothing_shop.entity.Order;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.repository.OrderRepository;
import com.shop.clothing_shop.service.SepayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SepayController {

    private final SepayService sepayService;
    private final OrderRepository orderRepo;


    @PostMapping("/sepay/ipn")
    public ResponseEntity<Void> handleIpn(@RequestBody SepayIpnRequest req) {

        log.info("SePay IPN raw: {}", req);

        String orderCode = req.getOrder().getOrderId();

        Order order = orderRepo.findByOrderCode(orderCode)
                .orElse(null);

        if (order == null) {
            log.warn("IPN test / unknown order: {}", orderCode);
            return ResponseEntity.ok().build();
        }

        if ("APPROVED".equals(req.getTransaction().getTransactionStatus())) {
            order.setStatus(OrderStatus.PAID);
            orderRepo.save(order);
        }

        return ResponseEntity.ok().build();
    }
}