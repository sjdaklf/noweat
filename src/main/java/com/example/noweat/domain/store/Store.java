package com.example.noweat.domain.store;

import com.example.noweat.domain.common.entity.BaseEntity;
import com.example.noweat.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@NoArgsConstructor
@Getter
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String storeName;

    private String storeAddress;

    @Enumerated(value = EnumType.STRING)
    private StoreCategory storeCategory;

    private Long minOrderPrice;

    private Double averageRating;

    private LocalTime openTime;

    private LocalTime closedTime;

    private boolean isClosed;

    @Builder
    public Store(User user, String storeName, String storeAddress, StoreCategory storeCategory, Long minOrderPrice, Double averageRating, LocalTime openTime, LocalTime closedTime, boolean isClosed) {
        this.user = user;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.averageRating = averageRating;
        this.openTime = openTime;
        this.closedTime = closedTime;
        this.isClosed = isClosed;
    }

    public void updateStore(String storeName, String storeAddress, StoreCategory storeCategory, Long minOrderPrice, LocalTime openTime, LocalTime closedTime) {
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storeCategory = storeCategory;
        this.minOrderPrice = minOrderPrice;
        this.openTime = openTime;
        this.closedTime = closedTime;
    }

    public void deleteStore(boolean isClosed) {
        this.isClosed = isClosed;
    }
}
