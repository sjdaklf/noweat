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

        // 삭제된 유저인지 확인
        verifyUser(findUser);

        Menu findMenu = menuRepository.findById(menuId).orElseThrow(() -> new NotFoundException(ErrorCode.MENU_NOT_EXIST));

        // 삭제된 메뉴는 주문할 수 없음
        verifyMenu(findMenu);

        Store findStore = findMenu.getStore();

        // 폐업된 가게면 주문을 생성할 수 없음
        verifyStore(findStore);

        // 가게 엽업시간이 아닐 때 주문 할 수 없음
        if(currentTime.isBefore(findStore.getOpenTime()) || currentTime.isAfter(findStore.getClosedTime())){
            throw new BadRequestException(ErrorCode.STORE_NOT_OPEN);
        }

        // 주문 금액이 최소 주문금액보다 적으면 예외 발생
        if(findStore.getMinOrderPrice() > findMenu.getPrice()){
            throw new BadRequestException(ErrorCode.ORDER_PRICE_TOO_LOW);
        }

        Order order = Order.builder()
                .user(findUser)
                .store(findStore)
                .orderStatus(OrderStatus.PENDING)
                .menuName(findMenu.getName())
                .menuPrice(findMenu.getPrice())
                .build();

        Order saveOrder = orderRepository.save(order);

        return OrderCreateResponseDto.builder()
                .id(saveOrder.getId())
                .storeId(findStore.getId())
                .orderStatus(saveOrder.getOrderStatus())
                .storeName(saveOrder.getStore().getName())
                .menuName(saveOrder.getMenuName())
                .menuPrice(saveOrder.getMenuPrice())
                .createdAt(saveOrder.getCreatedAt())
                .build();
    }

    @Transactional
    public OrderStatusUpdateResponseDto updateOrderStatus(AuthUser authUser, Long orderId, OrderStatusUpdateRequestDto orderStatusUpdateRequestDto){

        // 주문 상태에 맞지않는 값이면 예외
        OrderStatus orderStatus = OrderStatus.of(orderStatusUpdateRequestDto.getOrderStatus());

        // 사장님만 주문 상태를 변경할 수 있다.
        if(authUser.getUserRole() != UserRole.OWNER){
            throw new ForbiddenException(ErrorCode.NOT_OWNER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        // 사장님의 가게가 아니면 주문을 수락 할 수 없음
        if(authUser.getId() != findOrder.getStore().getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_STORE_OWNER);
        }

        findOrder.updateOrderStatus(orderStatus);

        return OrderStatusUpdateResponseDto.builder()
                .id(findOrder.getId())
                .storeId(findOrder.getStore().getId())
                .orderStatus(findOrder.getOrderStatus())
                .build();
    }

    @Transactional(readOnly = true)
    public List<OrderUserResponseDto> findUsersAllOrders(AuthUser authUser){
        // 유저의 주문한 목록을 볼 수 있음
        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        // 유저의 id로 주문 목록 찾기
        List<Order> orders = orderRepository.findByUser_Id(authUser.getId());

        return orders.stream().map(order -> OrderUserResponseDto.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus())
                .storeName(order.getStore().getName())
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

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        // 사장의 아이디로 주문 목록 찾기
        List<Order> orders = orderRepository.findByOwnerUserId(authUser.getId());

        return orders.stream().map(order -> OrderOwnerResponseDto.builder()
                .id(order.getId())
                        .orderUserId(order.getUser().getId())
                .orderStatus(order.getOrderStatus())
                .storeName(order.getStore().getName())
                .menuName(order.getMenuName())
                .menuPrice(order.getMenuPrice())
                .createdAt(order.getCreatedAt())
                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUsersOrder(AuthUser authUser, Long orderId){

        if(authUser.getUserRole() != UserRole.USER){
            throw new ForbiddenException(ErrorCode.NOT_USER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        if(authUser.getId() != findOrder.getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_USERS_ORDER);
        }

        if(findOrder.getOrderStatus() != OrderStatus.PENDING){
            throw new BadRequestException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
        }

        orderRepository.deleteById(orderId);
    }

    @Transactional
    public void deleteOwnersOrder(AuthUser authUser, Long orderId){

        if(authUser.getUserRole() != UserRole.OWNER){
            throw new ForbiddenException(ErrorCode.NOT_OWNER);
        }

        User findUser = userRepository.findById(authUser.getId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        verifyUser(findUser);

        Order findOrder = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_EXIST));

        if(authUser.getId() != findOrder.getStore().getUser().getId()){
            throw new ForbiddenException(ErrorCode.NOT_OWNERS_ORDER);
        }

        orderRepository.deleteById(orderId);

    }

    public void verifyUser(User findUser) {
        if (findUser.isDeleted()) {
            throw new GoneException(ErrorCode.USER_ALREADY_DELETED);
        }
    }

    public void verifyStore(Store findStore) {
        if (findStore.isClosed()) {
            throw new GoneException(ErrorCode.STORE_CLOSED);
        }
    }

    public void verifyMenu(Menu findMenu) {
        if (findMenu.isDeleted()) {
            throw new GoneException(ErrorCode.MENU_DELETED);
        }
    }
}
