package com.shop.clothing_shop.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class CursorPageResponse<T> {

    private List<T> items;
    private String nextCursor;
    private boolean hasNext;
}
