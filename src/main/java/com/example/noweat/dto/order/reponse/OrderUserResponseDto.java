package com.example.noweat.dto.order.reponse;

import com.example.noweat.domain.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderUserResponseDto {
    private final Long id;
    private final OrderStatus orderStatus;
    private final String storeName;
    private final String menuName;
    private final Long menuPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @Builder
    public OrderUserResponseDto(Long id, OrderStatus orderStatus, String storeName, String menuName, Long menuPrice, LocalDateTime createdAt) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.storeName = storeName;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.createdAt = createdAt;
    }
}
