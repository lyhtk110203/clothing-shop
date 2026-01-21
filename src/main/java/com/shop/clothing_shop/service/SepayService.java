package com.shop.clothing_shop.service;

import com.shop.clothing_shop.dto.request.SepayIpnRequest;
import com.shop.clothing_shop.entity.Order;
import com.shop.clothing_shop.entity.Payment;
import com.shop.clothing_shop.entity.PaymentTransaction;
import com.shop.clothing_shop.enums.OrderStatus;
import com.shop.clothing_shop.enums.PaymentStatus;
import com.shop.clothing_shop.exception.BusinessException;
import com.shop.clothing_shop.repository.OrderRepository;
import com.shop.clothing_shop.repository.PaymentRepository;
import com.shop.clothing_shop.repository.PaymentTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SepayService {

    private final PaymentRepository paymentRepo;
    private final PaymentTransactionRepository transactionRepo;
    private final OrderRepository orderRepo;

    @Value("${sepay.secret-key}")
    private String secretKey;

    @Transactional
    public void handleIpnRaw(Map<String, Object> payload) {

        String orderCode = (String) payload.get("orderCode");
        String status = (String) payload.get("status");
        String transactionCode = (String) payload.get("transactionCode");

        Order order = orderRepo.findByOrderCode(orderCode)
                .orElseThrow(() -> new BusinessException("Order not found"));

        Payment payment = paymentRepo.findByOrder_Id(order.getId())
                .orElseThrow(() -> new BusinessException("Payment not found"));

        if (transactionRepo.existsByTransactionCode(transactionCode)) {
            return;
        }

        PaymentTransaction tx = PaymentTransaction.builder()
                .gateway("SEPAY")
                .transactionCode(transactionCode)
                .status(status)
                .rawPayload(payload.toString())
                .payment(payment)
                .build();

        transactionRepo.save(tx);

        if ("SUCCESS".equalsIgnoreCase(status) || "PAID".equalsIgnoreCase(status)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
        }
    }


//    private boolean verifySignature(SepayIpnRequest req) {
//        try {
//            String data =
//                    req.getMerchantId()
//                            + req.getOrderCode()
//                            + req.getTransactionCode()
//                            + req.getAmount()
//                            + secretKey;
//
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
//
//            StringBuilder hex = new StringBuilder();
//            for (byte b : hash) {
//                hex.append(String.format("%02x", b));
//            }
//
//            return hex.toString().equalsIgnoreCase(req.getSignature());
//
//        } catch (Exception e) {
//            return false;
//        }
//    }
}
