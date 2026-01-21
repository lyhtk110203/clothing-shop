package com.shop.clothing_shop.service;

import com.shop.clothing_shop.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOrderTrackingEmail(Order order, String email) {

        String trackingUrl =
                "http://localhost:8080/api/orders/" + order.getTrackingToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Xác nhận đơn hàng " + order.getOrderCode());
        message.setText("""
                Xin chào %s,

                Đơn hàng của bạn đã được tạo thành công.

                Mã đơn: %s
                Trạng thái: %s
                Tổng tiền: %s

                Theo dõi đơn hàng:
                %s
                """.formatted(
                order.getCustomerName(),
                order.getOrderCode(),
                order.getStatus(),
                order.getTotalAmount(),
                trackingUrl
        ));

        mailSender.send(message);
    }
}
