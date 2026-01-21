package com.shop.clothing_shop.repository;

import com.shop.clothing_shop.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

    Optional<PaymentTransaction> findTopByPayment_Order_OrderCodeOrderByCreatedAtDesc(
            String orderCode
    );
    boolean existsByTransactionCode(String transactionCode);

}
