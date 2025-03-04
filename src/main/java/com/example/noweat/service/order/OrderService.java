package com.example.noweat.service.order;

import com.example.noweat.domain.menu.Menu;
import com.example.noweat.domain.order.Order;
import com.example.noweat.domain.order.OrderStatus;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import com.example.noweat.domain.user.UserRole;
import com.example.noweat.dto.order.reponse.OrderCreateResponseDto;
import com.example.noweat.dto.order.reponse.OrderOwnerResponseDto;
import com.example.noweat.dto.order.reponse.OrderUserResponseDto;
import com.example.noweat.dto.order.reponse.OrderStatusUpdateResponseDto;
import com.example.noweat.dto.order.request.OrderStatusUpdateRequestDto;
import com.example.noweat.global.argumentResolver.AuthUser;
import com.example.noweat.repository.menu.MenuRepository;
import com.example.noweat.repository.order.OrderRepository;
import com.example.noweat.repository.store.StoreRepository;
import com.example.noweat.repository.user.UserRepository;
import com.example.noweat.service.exception.BadRequestException;
import com.example.noweat.service.exception.ForbiddenException;
import com.example.noweat.service.exception.GoneException;
import com.example.noweat.service.exception.NotFoundException;
import com.example.noweat.service.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public OrderCreateResponseDto createOrder(AuthUser authUser, Long menuId){

        LocalTime currentTime = LocalTime.now();

        // 유저만 주문을 생성할 수 있음
        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        if(findUser.isDeleted()){
            throw new GoneException(ErrorCode.USER_ALREADY_DELETED);
        }

        Menu findMenu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException(ErrorCode.MENU_NOT_EXIST));
        Store findStore = findMenu.getStore();

        // 폐업된 가게면 주문을 생성할 수 없음
        if(findStore.isClosed()){
            throw new GoneException(ErrorCode.STORE_CLOSED);
        }

        // 가게 엽업시간이 아닐 때 주문 할 수 없음
        if(currentTime.isBefore(findStore.getOpenTime()) || currentTime.isAfter(findStore.getClosedTime())){
            throw new BadRequestException(ErrorCode.STORE_NOT_OPEN);
        }

        // 삭제된 메뉴는 주문할 수 없음
        if(findMenu.isDeleted()){
            throw new GoneException(ErrorCode.MENU_DELETED);
        }

        // 주문 금액이 최소 주문금액보다 적으면 예외 발생
        if(findStore.getMinOrderPrice() > findMenu.getMenuPrice()){
            throw new BadRequestException(ErrorCode.ORDER_PRICE_TOO_LOW);
        }

        Order order = Order.builder()
                .user(findUser)
                .store(findStore)
                .orderStatus(OrderStatus.PENDING)
                .menuName(findMenu.getMenuName())
                .menuPrice(findMenu.getMenuPrice())
                .build();

        Order saveOrder = orderRepository.save(order);

        return OrderCreateResponseDto.builder()
                .id(saveOrder.getId())
                .orderStatus(saveOrder.getOrderStatus())
                .storeName(saveOrder.getStore().getStoreName())
                .menuName(saveOrder.getMenuName())
                .menuPrice(saveOrder.getMenuPrice())
                .createdAt(saveOrder.getCreatedAt())
                .build();
    }

    @Transactional
    public OrderStatusUpdateResponseDto updateOrderStatus(AuthUser authUser, Long orderId, OrderStatusUpdateRequestDto orderStatusUpdateRequestDto){

        // 사장님만 주문 상태를 변경할 수 있다.
        if(authUser.getUserRole() != UserRole.OWNER){
            throw new ForbiddenException(ErrorCode.NOT_OWNER);
        }

        // 주문 상태에 맞지않는 값이면 예외
        OrderStatus orderStatus = OrderStatus.of(orderStatusUpdateRequestDto.getOrderStatus());

        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        // 사장님의 가게가 아니면 주문을 수락 할 수 없음
        if(authUser.getId() != findOrder.getStore().getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_STORE_OWNER);
        }

        findOrder.updateOrderStatus(orderStatus);

        return OrderStatusUpdateResponseDto.builder()
                .orderStatus(findOrder.getOrderStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<OrderUserResponseDto> findUsersAllOrders(AuthUser authUser){
        // 유저의 주문한 목록을 볼 수 있음
        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        // 유저의 id로 주문 목록 찾기
        List<Order> orders = orderRepository.findByUser_Id(authUser.getId());

        return orders.stream().map(order -> OrderUserResponseDto.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .storeName(order.getStore().getStoreName())
                .menuName(order.getMenuName())
                .menuPrice(order.getMenuPrice())
                .createdAt(order.getCreatedAt())
                .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderOwnerResponseDto> findOwnersAllOrders(AuthUser authUser){
        // 유저의 주문한 목록을 볼 수 있음
        if(authUser.getUserRole() != UserRole.OWNER){
            throw new ForbiddenException(ErrorCode.NOT_OWNER);
        }

        // 사장의 아이디로 주문 목록 찾기
        List<Order> orders = orderRepository.findByOwnerUserId(authUser.getId());

        return orders.stream().map(order -> OrderOwnerResponseDto.builder()
                .id(order.getId())
                        .orderUserId(order.getUser().getId())
                .orderStatus(order.getOrderStatus())
                .storeName(order.getStore().getStoreName())
                .menuName(order.getMenuName())
                .menuPrice(order.getMenuPrice())
                .createdAt(order.getCreatedAt())
                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUsersOrder(AuthUser authUser, Long orderId){

        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        if(authUser.getId() != findOrder.getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_USERS_ORDER);
        }

        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void deleteOwnersOrder(AuthUser authUser, Long orderId){

        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        if(authUser.getUserRole() != UserRole.OWNER){
            throw new ForbiddenException(ErrorCode.NOT_OWNER);
        }

        if(authUser.getId() != findOrder.getStore().getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_OWNERS_ORDER);
        }

        orderRepository.deleteById(orderId);

    }
}
