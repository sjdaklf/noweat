package com.example.noweat.domain.order;


import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.enums.ErrorCode;
import com.sun.jdi.request.InvalidRequestStateException;

import java.util.Arrays;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    DELIVERING,
    COMPLETED;

    public static OrderStatus of(String orderStatus) {
        return Arrays.stream(OrderStatus.values())
                .filter(r -> r.name().equalsIgnoreCase(orderStatus))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.INVALID_ORDER_STATUS));
    }
}
