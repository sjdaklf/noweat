package com.example.noweat.controller.order;

import com.example.noweat.dto.order.reponse.OrderCreateResponseDto;
import com.example.noweat.dto.order.reponse.OrderStatusUpdateResponseDto;
import com.example.noweat.dto.order.request.OrderStatusUpdateRequestDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/stores/{storeId}/menus/{menuId}/orders")
    public ResponseEntity<OrderCreateResponseDto> createOrder(AuthUser authUser, @PathVariable("storeId") Long storeId, @PathVariable("menuId") Long menuId){
        OrderCreateResponseDto orderCreateResponseDto = orderService.createOrder(authUser, storeId, menuId);
        return new ResponseEntity<>(orderCreateResponseDto, HttpStatus.OK);
    }

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<OrderStatusUpdateResponseDto> updateOrderStatus(AuthUser authUser, @PathVariable("orderId") Long orderId, @Valid @RequestBody OrderStatusUpdateRequestDto orderStatusUpdateRequestDto){
        OrderStatusUpdateResponseDto orderStatusUpdateResponseDto = orderService.updateOrderStatus(authUser, orderId, orderStatusUpdateRequestDto);
        return new ResponseEntity<>(orderStatusUpdateResponseDto, HttpStatus.OK);
    }
}
