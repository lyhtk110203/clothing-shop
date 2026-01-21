package com.shop.clothing_shop.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderEmailDto {
    private String to;
    private String subject;
    private String content;
}

