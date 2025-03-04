package com.example.noweat.dto.order.reponse;

import com.example.noweat.domain.order.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderStatusUpdateResponseDto {
    private final OrderStatus orderStatus;

    @Builder
    public OrderStatusUpdateResponseDto(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}
