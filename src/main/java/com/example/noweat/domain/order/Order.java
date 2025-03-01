package com.example.noweat.domain.order;

import com.example.noweat.domain.common.entity.BaseEntity;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    private String menuName;

    private Long menuPrice;

    @Builder
    public Order(User user, Store store, OrderStatus orderStatus, String menuName, Long menuPrice) {
        this.user = user;
        this.store = store;
        this.orderStatus = orderStatus;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
    }
}
