package com.shop.clothing_shop.dto.request;

import com.shop.clothing_shop.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {
    private OrderStatus status;
}
