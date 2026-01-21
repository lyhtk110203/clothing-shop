package com.shop.clothing_shop.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class SepayIpnRequest {

    private Long timestamp;

    @JsonProperty("notification_type")
    private String notificationType;

    private OrderData order;
    private TransactionData transaction;

    @Getter @Setter
    public static class OrderData {
        private String id;

        @JsonProperty("order_id")
        private String orderId;

        @JsonProperty("order_status")
        private String orderStatus;

        @JsonProperty("order_amount")
        private BigDecimal orderAmount;

        @JsonProperty("order_invoice_number")
        private String invoiceNumber;
    }

    @Getter @Setter
    public static class TransactionData {
        private String id;

        @JsonProperty("transaction_id")
        private String transactionId;

        @JsonProperty("transaction_status")
        private String transactionStatus;

        @JsonProperty("transaction_amount")
        private BigDecimal transactionAmount;
    }
}
