package com.example.noweat.dto.order.request;

import com.example.noweat.domain.order.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class OrderStatusUpdateRequestDto {
    @NotBlank(message = "주문 상태는 필수 입력 값 입니다.")
    private String orderStatus;
}
