package com.example.noweat.domain.order.enums;


import com.sun.jdi.request.InvalidRequestStateException;
import org.hibernate.query.Order;

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
                .orElseThrow(() -> new InvalidRequestStateException("유효하지 않은 OrderStatus"));
    }
}
