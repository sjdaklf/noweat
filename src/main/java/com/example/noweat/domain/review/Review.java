package com.example.noweat.domain.review;

import com.example.noweat.domain.common.entity.BaseEntity;
import com.example.noweat.domain.order.Order;
import com.example.noweat.domain.store.Store;
import com.example.noweat.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String title;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private StarRating starRating;

    @Builder
    public Review(User user, Store store, Order order, String title, String content, StarRating starRating) {
        this.user = user;
        this.store = store;
        this.order = order;
        this.title = title;
        this.content = content;
        this.starRating = starRating;
    }
}
