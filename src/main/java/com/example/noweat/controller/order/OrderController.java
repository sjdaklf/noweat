package com.example.noweat.controller.order;

import com.example.noweat.dto.order.reponse.OrderCreateResponseDto;
import com.example.noweat.dto.order.reponse.OrderStatusUpdateResponseDto;
import com.example.noweat.dto.order.reponse.OrderUserResponseDto;
import com.example.noweat.dto.order.request.OrderStatusUpdateRequestDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/menus/{menuId}/orders")
    public ResponseEntity<OrderCreateResponseDto> createOrder(AuthUser authUser, @PathVariable("menuId") Long menuId){
        OrderCreateResponseDto orderCreateResponseDto = orderService.createOrder(authUser, menuId);
        return new ResponseEntity<>(orderCreateResponseDto, HttpStatus.OK);
    }

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<OrderStatusUpdateResponseDto> updateOrderStatus(AuthUser authUser, @PathVariable("orderId") Long orderId, @Valid @RequestBody OrderStatusUpdateRequestDto orderStatusUpdateRequestDto){
        OrderStatusUpdateResponseDto orderStatusUpdateResponseDto = orderService.updateOrderStatus(authUser, orderId, orderStatusUpdateRequestDto);
        return new ResponseEntity<>(orderStatusUpdateResponseDto, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderUserResponseDto>> findUsersAllOrders(AuthUser authUser){
        return new ResponseEntity<>(orderService.findUsersAllOrders(authUser), HttpStatus.OK);
    }

    @DeleteMapping("/orders/{orderId}/users")
    public ResponseEntity<Void> deleteUsersOrder(AuthUser authUser, @PathVariable("orderId") Long orderId){
        orderService.deleteUsersOrder(authUser, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/orders/{orderId}/owners")
    public ResponseEntity<Void> deleteOwnersOrder(AuthUser authUser, @PathVariable("orderId") Long orderId){
        orderService.deleteOwnersOrder(authUser, orderId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
