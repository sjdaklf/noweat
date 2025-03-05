package com.example.noweat.dto.order.reponse;

import com.example.noweat.domain.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderStatusUpdateResponseDto {
    private final Long id;
    private final Long storeId;
    private final OrderStatus orderStatus;

    @Builder
    public OrderStatusUpdateResponseDto(Long id, Long storeId, OrderStatus orderStatus) {
        this.id = id;
        this.storeId = storeId;
        this.orderStatus = orderStatus;
    }
}
