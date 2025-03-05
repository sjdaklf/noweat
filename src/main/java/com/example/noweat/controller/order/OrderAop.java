package com.example.noweat.controller.order;

import com.example.noweat.dto.order.reponse.OrderCreateResponseDto;
import com.example.noweat.dto.order.reponse.OrderStatusUpdateResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
@Slf4j
public class OrderAop {
    @Around("execution(* com.example.noweat.controller.order.OrderController.createOrder(..))")
    public Object createOrderLog(ProceedingJoinPoint joinPoint) throws Throwable {

        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        log.info("주문 요청 시각 = {}", dateTimeFormatter.format(localDateTime));

        Object result = joinPoint.proceed();
        ResponseEntity<OrderCreateResponseDto> responseEntity = (ResponseEntity<OrderCreateResponseDto>)result;

        log.info("orderId = {}, storeId = {}", responseEntity.getBody().getId(), responseEntity.getBody().getStoreId());

        return result;
    }

    @Around("execution(* com.example.noweat.controller.order.OrderController.updateOrderStatus(..))")
    public Object updateOrderStatusLog(ProceedingJoinPoint joinPoint) throws Throwable{
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        log.info("주문 상태 변경 요청 시각 = {}", dateTimeFormatter.format(localDateTime));

        Object result = joinPoint.proceed();
        ResponseEntity<OrderStatusUpdateResponseDto> responseEntity = (ResponseEntity<OrderStatusUpdateResponseDto>)result;

        log.info("orderId = {}, storeId = {}", responseEntity.getBody().getId(), responseEntity.getBody().getStoreId());

        return result;
    }
}
